var file = function(){

    var buttonInit = function () {
        $("#uploadFile").on("click", function(){

            /*$("#myModalLabel").empty();
            $("#myModalLabel").append("�ϴ��ļ�");

            $("#myModalBody").empty();
            $("#myModalBody").append('<div id="fileUploadContent" class="fileUploadContent"></div>');*/

            $("#fileUpload").modal();
            $("#fileUploadContent").initUpload({
                "uploadUrl": "/file/fileUpload",
                //�ϴ��ļ���Ϣ��ַ
                //"progressUrl": "/file/getStatus",
                "filelSavePath": "test"
            });
            uploadFileList.fileList.splice(0, uploadFileList.fileList.length);
        });

        $("#createDir").on("click", function(){
            $("#createDirModal").modal("show");
        })
    };
    
    return{
        init : function () {
            buttonInit();
        }
    }
    
}();