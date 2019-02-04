package cn.stx.bookstore.book.web.servlet.admin;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.itcast.commons.CommonUtils;
import cn.stx.bookstore.book.domain.Book;
import cn.stx.bookstore.book.service.BookService;
import cn.stx.bookstore.category.domain.Category;
import cn.stx.bookstore.category.service.CategoryService;

/**
 * Servlet implementation class AdminAddBookServlet
 */
@WebServlet("/admin/AdminAddBookServlet")
public class AdminAddBookServlet extends HttpServlet {
	private BookService bookService = new BookService();
	private CategoryService categoryService = new CategoryService();

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 把表单数据封装到Book对象中
		 * 上传三步
		 */
		//创建工厂
		DiskFileItemFactory factory = new DiskFileItemFactory(15*1024, new File("F:/f/temp"));
		//得到解析器
		ServletFileUpload sfu = new ServletFileUpload(factory);
		//设置单个文件大小为15kb
		sfu.setFileSizeMax(15*1024);
		//使用解析器接续request对象，得到list
		try {
			List<FileItem> fileItemList = sfu.parseRequest(request);
			/*
			 * 把fileItemList封装到Book对象中
			 * 	1.把所有的普通表单字段封装到map中
			 *  2.再把map中的数据封装到Book中
			 */
			//将所有的文字表单项封装到book对象中
			Map<String, String> map = new HashMap<String, String>();
			for(FileItem fileItem:fileItemList) {
				if(fileItem.isFormField()) {
					map.put(fileItem.getFieldName(), fileItem.getString("utf-8"));
				}
			}
			Book book = CommonUtils.toBean(map, Book.class);
			/*
			 * 需要把map中的cid，封装到category对象中，然后赋值给book
			 */
			Category category = CommonUtils.toBean(map, Category.class);
			book.setCategory(category);
			book.setBid(CommonUtils.uuid());
			/*
			 * 保存上传的文件
			 *  1.保存的路径
			 *  2.保存的文件名称
			 */
			//得到要保存的目录
			String savepath = this.getServletContext().getRealPath("/book_img");
			//得到文件名称，给原来的文件添加uuid前缀，避免文件名冲突
			String filename = CommonUtils.uuid()+"_"+fileItemList.get(1).getName();
			/*
			 * 校验文件的扩展名，只允许jpg
			 */
			if(!filename.toLowerCase().endsWith(".jpg")) {
				request.setAttribute("categoryList", categoryService.findAll());
				request.setAttribute("msg", "您上传的文件不是jpg扩展名");
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp").forward(request, response);;
				return;
			}
			//使用目录和文件名称，创建目标文件
			File destFile = new File(savepath,filename);
			//保存上传文件到目标文件位置
			fileItemList.get(1).write(destFile);
			/*
			 * 设置book对象的image，即把图片的路径设置给book的image
			 */
			book.setImage("book_img/"+filename);
			/*
			 * 校验图片的尺寸
			 */
			Image image = new ImageIcon(destFile.getAbsolutePath()).getImage();
			if(image.getWidth(null)>200||image.getHeight(null)>200) {
				destFile.delete();
				request.setAttribute("categoryList", categoryService.findAll());
				request.setAttribute("msg", "您上传的文件尺寸超出了200*200");
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp").forward(request, response);
				return;
			}
			/*
			 * 使用bookService完成保存
			 */
			bookService.add(book);
			request.getRequestDispatcher("/admin/AdminBookServlet?method=findAll").forward(request, response);
		} catch(Exception e) {
			if(e instanceof FileUploadBase.FileSizeLimitExceededException){
				request.setAttribute("categoryList", categoryService.findAll());
				request.setAttribute("msg", "您上传的文件超出了15kb");
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp").forward(request, response);
			}
		}
	}

}
