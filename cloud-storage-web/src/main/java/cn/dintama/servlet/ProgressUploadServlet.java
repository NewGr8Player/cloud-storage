package cn.dintama.servlet;

import cn.dintama.listener.UploadListener;
import cn.dintama.utils.dto.UploadStatus;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ProgressUploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ServletContext sc;
	private String savePath;
	public void init(ServletConfig config) throws ServletException {
		savePath = config.getInitParameter("savePath");
		sc = config.getServletContext();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		 response.setHeader("Cache-Control", "no-store");  
	        response.setHeader("Pragrma", "no-cache");  
	        response.setDateHeader("Expires", 0);  
	        response.setContentType("text/html;charset=utf-8");  
	        UploadStatus status = (UploadStatus) request.getSession(true)
	                .getAttribute("upload_status");  
	      
	        if (status == null) {  
	            response.getWriter().println("û���ϴ���Ϣ");  
	              
	            return;  
	        }  
	        int percent=status.getPercent();
	        long length=status.getBytesRead()/1024/1204;
	        long totalLength=status.getContentLength()/1204/1024;
	        long time=status.getUseTime();
	        long velocity=status.getBytesRead()/time;
	        long totalTime=status.getContentLength()/velocity;
	        long timeLeft=totalTime-time;
	  
	        // ��ʽ���ٷֱ�||�������(M)||�ļ��ܳ���(M)||��������(K)||����ʱ��(s)||������ʱ��(s)||����ʣ��ʱ��(s)||�����ϴ��ڼ����ļ�  
	        String value = percent + "||" + length + "||" + totalLength + "||"  
	                + velocity + "||" + time + "||" + totalTime + "||" + timeLeft  
	                + "||" + status.getItems();  
	  
	        response.getWriter().println(value);  
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		UploadListener listener=new UploadListener(session);
		DiskFileItemFactory factory=new DiskFileItemFactory();
		ServletFileUpload upload=new ServletFileUpload(factory);
		upload.setProgressListener(listener);
		try{
			List itemList=upload.parseRequest(request);
			Iterator itr=itemList.iterator();
			while(itr.hasNext()){
				FileItem item=(FileItem)itr.next();
				if(item.isFormField()){
					System.out.println("������"+item.getFieldName()+"="+item.getString("UTF-8"));
				}else{
					if(item.getName()!=null&&!item.getName().equals("")){
						File tempFile = new File(item.getName());
						File file=new File(sc.getRealPath("/")+"\\"+savePath,tempFile.getName());
						item.write(file);
						response.getWriter().println("�ļ����ϴ�"+file.getName());
					}
				}
			}
		}catch(Exception e){
			response.getWriter().println("�ϴ��ļ�ʧ�ܣ�"+e.getMessage());
		}
	}

	

}
