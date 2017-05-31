package cn.dintama.controller;

import cn.dintama.dao.FileDao;
import cn.dintama.entity.FileDo;
import cn.dintama.entity.User;
import cn.dintama.utils.HDFSUtil;
import cn.dintama.utils.dto.UploadStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Dintama on 2017/5/27.
 */
@Controller
public class WorkbenchController {


    @Resource
    private FileDao fileDao;

    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, Model model){
        User user = (User)request.getSession().getAttribute("user");
        model.addAttribute("nickname", user.getNickname());
        return "workbench/index";
    }


    @RequestMapping(value = "/file/fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam(value="file",required= false) MultipartFile[] files,HttpServletRequest request) throws IOException {

        DecimalFormat df = new DecimalFormat("######0.00");

        String curTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File pathFile = new File("/tmp/CloudStorageFile/"+curTime);

        Integer parentId = Integer.parseInt(request.getParameter("pathId"));

        User user = (User)request.getSession().getAttribute("user");
        if(user == null || user.getEmail() == null || user.getEmail() == ""){
            return "failed";
        }

        String email = user.getEmail();

        String hdfsPath = "/cloud-storage/" + email + "/";

        if(!pathFile.exists()&&!pathFile.isDirectory()){
            pathFile.mkdirs();
        }
        if(files!=null&&files.length>0){
            for(int i = 0;i<files.length;i++){

                MultipartFile file = files[i];

                String tmpFileName = new BASE64Encoder().encode((email + file.getOriginalFilename()).getBytes());
                String filePath = "/tmp/CloudStorageFile/"+curTime+"/"+tmpFileName;

                file.transferTo(new File(filePath));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HDFSUtil.updateHdfs(filePath, hdfsPath + file.getOriginalFilename());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                //FileDo构造
                FileDo fileDo = new FileDo();
                fileDo.setUserId(user.getId());
                fileDo.setParentId(parentId);
                fileDo.setFileName(file.getOriginalFilename());
                fileDo.setHdfsPath(hdfsPath + file.getOriginalFilename());
                long size = file.getSize();
                double finalSize = size / 1048576.0;
                fileDo.setFileSize(Double.parseDouble(df.format(finalSize)));

                fileDao.insertFile(fileDo);

            }
        }
        return "success";
    }

    @RequestMapping(value = "/file/getStatus")
    @ResponseBody
    public UploadStatus getStatus(HttpSession session){
        System.out.println((UploadStatus)session.getAttribute("upload_status"));
        return (UploadStatus)session.getAttribute("upload_status");
    }

    @RequestMapping(value = "/file/createDir")
    @ResponseBody
    public void createDir(HttpServletRequest request, FileDo fileDo){
        User user = (User)request.getSession().getAttribute("user");
        fileDo.setUserId(user.getId());
        fileDao.insertDir(fileDo);
    }

    @RequestMapping(value = "/file/listPage")
    @ResponseBody
    public List<FileDo> listPage(HttpServletRequest request, FileDo fileDo){
        User user = (User)request.getSession().getAttribute("user");
        fileDo.setUserId(user.getId());
        List<FileDo> fileDos = fileDao.selectAllFileListPage(fileDo);
        return fileDos;
    }

    @RequestMapping("/file/delete")
    @ResponseBody
    public void deleteFile(HttpServletRequest request, FileDo fileDo){
        fileDao.deleteFileById(fileDo);
    }

    @RequestMapping("file/renameDir")
    @ResponseBody
    public void renameDir(HttpServletRequest request, FileDo file){
        fileDao.updateFileNameById(file);
    }

    @RequestMapping("file/download")
    @ResponseBody
    public String downloadFile(HttpServletRequest request, FileDo file) throws UnsupportedEncodingException {
        FileDo fileDo = fileDao.selectFileById(file);
        String result = "http://www.hope6537.com:50075/webhdfs/v1"+ fileDo.getHdfsPath() +"?op=OPEN&namenoderpcaddress=www.hope6537.com:9000&offset=0";
        return result;
    }

    /*public static void main(String[] args) throws UnsupportedEncodingException {
        String decode = URLDecoder.decode("/cloud-storage/dingyi6680@qq.com/%E6%94%BE%E5%BC%80%E9%82%A3%E4%B8%AA%E5%A5%B3%E5%B7%AB%20www.80txt.com(all).txt", "UTF-8");
        System.out.println(decode);
    }*/

}
