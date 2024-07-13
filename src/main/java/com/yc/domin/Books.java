package com.yc.domin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Books {
    private Integer id;
    private String bookname;
    private String bookpress;
    private String pressdate;
    private String bookauthor;
    private Integer bookcount;
    private String bookimage;
}
