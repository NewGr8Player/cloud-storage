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
        });

        $("#confirmCreateDir").on("click", function () {
            $.ajax({
                url: "file/createDir",
                dataType: "json",
                data: {
                    parentId: $("#pathId").val(),
                    fileName: $("#dirName").val()
                },
                type: "POST",
                async:false,
                success: function (res) {
                    $.msgUtil.successMsg("�����ɹ���", "");
                },
                error: function () {
                    $.msgUtil.errorMsg("����ʧ�ܣ�", "��ˢ�º����ԡ�")
                }
            });
            $("#createDirModal").modal("hidden");
        });
    };
    
    return{
        init : function () {
            buttonInit();
        }
    }
    
}();