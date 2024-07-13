package com.yc.servlets;


import com.yc.dao.DbHelper;
import com.yc.domin.Books;
import com.yc.servlets.model.JsonModel;
import com.yc.utils.EncryptUtils;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

//loadOnStartup = -1
@WebServlet(value = "/books.action")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class BooksServlet extends BaseServlet {

    private DbHelper db = new DbHelper();

    private static final String UPLOAD_DIRECTORY = "images";//上传的目录名

    /**
     * 带头像的注册
     *
     * @param req
     * @param resp
     * @throws Exception
     */
    protected String upload(HttpServletRequest req, HttpServletResponse resp, Part picPart) throws Exception {
        //获取项目路径               //绝对路径   tomcat下weapps/项目名/uploads
        String uploadPath = req.getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;

        //如果路径不存在则创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        String name = picPart.getSubmittedFileName();//文件名  a.jpg
        picPart.write(uploadPath + File.separator + name);//保存文件到服务器硬盘

        //此图片的访问路径url
        String scheme = req.getScheme();//http
        String serverName = req.getServerName();//localhost
        int serverPort = req.getServerPort();//8080
        String contextPath = req.getContextPath();//项目
        //http     ://    localhost     :     8080        /项目名
        String fullUrl = scheme + "://" + serverName + ":" + serverPort + contextPath;

        String url = fullUrl+File.separator+UPLOAD_DIRECTORY+File.separator+name;
        System.out.println("图片访问路径为："+url);
        return url;
    }

    /**
     * 添加
     */
    protected void add(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JsonModel jm = new JsonModel();

        //取出参数
        String bookname = req.getParameter("bookname");
        String bookpress = req.getParameter("bookpress");
        String pressdate = req.getParameter("pressdate");
        String bookauthor = req.getParameter("bookauthor");
        String bookcount = req.getParameter("bookcount");
        Part picPart = req.getPart("image");
        String upload = upload(req, resp, picPart);

        int result = db.doUpdate("insert into books(bookname,bookpress,pressdate,bookauthor,bookcount,bookimage) values(?,?,?,?,?,?)",bookname,bookpress,pressdate,bookauthor,bookcount,upload);
        if (result > 0) {
            jm.setCode(1);
        } else {
            jm.setCode(0);
            //jm.setError("注册失败");
        }
        try {
            writerJson(jm, resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改
     * @param req
     * @param resp
     * @throws Exception
     */
    protected void modifyById(HttpServletRequest req,HttpServletResponse resp) throws Exception{


        String id = req.getParameter("id");
        String bookname = req.getParameter("bookname");
        String bookpress = req.getParameter("bookpress");
        String pressdate = req.getParameter("pressdate");
        String bookauthor = req.getParameter("bookauthor");
        String bookcount = req.getParameter("bookcount");
        Part picPart = req.getPart("image");
        String upload = upload(req, resp, picPart);

        String sql = "update books set bookname=?,bookpress=?,pressdate=?,bookauthor=?,bookcount=?,bookimage=? where id=?";
        db.doUpdate(sql,bookname,bookpress,pressdate,bookauthor,bookcount,upload,id);

        JsonModel jm = new JsonModel();
        jm.setCode(1);
        writerJson(jm,resp);
    }

    /**
     * 查询全部
     * @param req
     * @param resp
     * @throws Exception
     */
    protected void getAllBooks(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        List<Books> select = db.select(Books.class, "select * from books");
        select.forEach(s->{
            System.out.println(s);
        });
        JsonModel jm = new JsonModel();
        jm.setCode(1);
        jm.setObj(select);
        writerJson(jm,resp);
    }

    /**
     * 按条件查询
     * @param req
     * @param resp
     * @throws Exception
     */
    protected void selectByConditions(HttpServletRequest req,HttpServletResponse resp) throws Exception {
        String id = req.getParameter("id");
        String bookname = req.getParameter("bookname");
        String bookpress = req.getParameter("bookpress");
        String pressdate = req.getParameter("pressdate");
        String bookauthor = req.getParameter("bookauthor");

        String sql = "select * from books where 1=1";
        List<Object> params = new ArrayList<>();
        if (id != null && id.trim().isEmpty() == false){
            id = id.trim();
            params.add(id);
            sql += " and id=?";
        }
        if (bookname != null && bookname.trim().isEmpty() == false){
            bookname = bookname.trim();
            params.add(bookname);
            sql += " and bookname like concat('%',?,'%') ";
        }
        if (bookpress != null && bookpress.trim().isEmpty() == false){
            bookpress = bookpress.trim();
            params.add(bookpress);
            sql += " and bookpress like concat('%',?,'%') ";
        }
        if (pressdate != null && pressdate.trim().isEmpty() == false){
            pressdate = pressdate.trim();
            Date d = Date.valueOf(pressdate);
            params.add(d);
            sql += " and pressdate = ?";
        }
        if (bookauthor != null && bookauthor.trim().isEmpty() == false){
            bookauthor = bookauthor.trim();
            params.add(bookauthor);
            sql += " and bookauthor like concat('%',?,'%') ";
        }
        System.out.println(sql);
        List<Books> select = null;
        if (params.size()<=0 || params == null){
            select = db.select(Books.class, sql);
        }else{
            select = db.select(Books.class, sql, params.toArray());
            //sql = "select * from books where 1=1 and id=1";
            //select = db.select(Books.class, sql);
        }
        select.forEach(s->{
            System.out.println(s);
        });
        JsonModel jm = new JsonModel();
        jm.setObj(select);
        jm.setCode(1);
        writerJson(jm,resp);
    }
}
