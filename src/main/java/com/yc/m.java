package com.yc;

import com.yc.dao.DbHelper;
import com.yc.domin.Books;

import java.util.List;

public class m {
    public static void main(String[] args) throws Exception {
        DbHelper db = new DbHelper();
        List<Books> select = db.select(Books.class, "select * from books");
        select.forEach(s->{
            System.out.println(s);
        });
    }
}
