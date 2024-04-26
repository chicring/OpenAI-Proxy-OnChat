package com.hjong.OnChat.entity.vo.resp;

import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> {

     private Long total;
     private Integer pageSize;
     private Integer currentPage;
     private Integer totalPages;

     private List<T> list;
}
