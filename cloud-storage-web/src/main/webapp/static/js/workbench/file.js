var file = function(){

    var buttonInit = function () {
        $("#uploadFile").on("click", function(){
            $("#fileUpload").modal();
            $("#fileUploadContent").initUpload({
                "uploadUrl": "/file/fileUpload",
                //�ϴ��ļ���Ϣ��ַ
                //"progressUrl": "/file/getStatus",
                "filelSavePath": "test"
            });
            uploadFileList.fileList.splice(0, uploadFileList.fileList.length);
        });
    };
    
    return{
        init : function () {
            buttonInit();
        }
    }
    
}();