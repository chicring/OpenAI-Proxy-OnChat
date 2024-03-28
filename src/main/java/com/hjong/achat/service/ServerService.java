package com.hjong.achat.service;

import com.hjong.achat.entity.DTO.Server;
import com.hjong.achat.entity.VO.resp.StatusInfoVO;
import com.hjong.achat.repositories.ChannelRepositories;
import com.hjong.achat.repositories.UserRepositories;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import reactor.core.publisher.Mono;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/28
 **/

@Service
public class ServerService {

    @Resource
    ChannelRepositories channelRepositories;
    @Resource
    UserRepositories userRepositories;


    public Mono<StatusInfoVO> status() throws InterruptedException {
        Mono<Long> channelCount = channelRepositories.count();
        Mono<Long> userCount = userRepositories.count();

        Mono<Server> serverInfo = getServerInfo();

        return Mono.zip(channelCount, userCount, serverInfo)
                .map(tuple -> {
                    StatusInfoVO statusInfoVO = new StatusInfoVO();
                    statusInfoVO.setChannelAmount(tuple.getT1());
                    statusInfoVO.setUserAmount(tuple.getT2());
                    statusInfoVO.setServer(tuple.getT3());
                    return statusInfoVO;
                });
    }

    private Mono<Server> getServerInfo() throws InterruptedException {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hardware = si.getHardware();
        CentralProcessor processor = si.getHardware().getProcessor();
        GlobalMemory memory = si.getHardware().getMemory();
        OperatingSystem os = si.getOperatingSystem();

        // CPU信息
        CentralProcessor centralProcessor = hardware.getProcessor();
        // 获取CPU核心数
        int physicalProcessorCount = centralProcessor.getPhysicalProcessorCount();

        //总内存
        long totalOfG = memory.getTotal() / 1024 / 1024 / 1024;
        // 可用大小
        long availableOfG = memory.getAvailable() / 1024 / 1024 / 1024;

        long uptimeInSeconds = si.getOperatingSystem().getSystemUptime();
        long days = uptimeInSeconds / (3600 * 24);
        long hours = (uptimeInSeconds % (3600 * 24)) / 3600;
        long minutes = (uptimeInSeconds % 3600) / 60;

        Runtime runtime = Runtime.getRuntime();
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        LocalDateTime jvmStartTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        Duration duration = Duration.between(jvmStartTime, LocalDateTime.now());
        long totalMillis = duration.toMillis();
        long days1 = totalMillis / (1000 * 60 * 60 * 24);
        totalMillis %= (1000 * 60 * 60 * 24);
        long hours1 = totalMillis / (1000 * 60 * 60);
        totalMillis %= (1000 * 60 * 60);
        long minutes1 = totalMillis / (1000 * 60);

        Server server = new Server();
        server.setCoreCount(physicalProcessorCount + " core");
        server.setMemoryTotal(totalOfG + "G");
        server.setMemoryUsed((totalOfG - availableOfG) + "G");
        DecimalFormat df = new DecimalFormat("#.00");
        double memoryUsage = (double) ((totalOfG - availableOfG) * 100) / totalOfG;
        server.setMemoryUsage(Double.parseDouble(df.format(memoryUsage)));
        server.setRunTimeProject(String.format("%sd %sh %sm", days1, hours1, minutes1));
        server.setRunTimeOS(days + "d" + " " + hours + "h" + " " + minutes + " " + "m");
        return Mono.just(server);
    }

}

