package com.example.shaad.downupml;

class DownUpFile {
    private String mFileName;
    private String mFileType;
    private String mFileSize;
    private String mFileDelete;
    private String mFileDownload;

    DownUpFile() {
    }

    public DownUpFile(String mFileName, String mFileType, String mFileSize) {
        this.mFileName = mFileName;
        this.mFileType = mFileType;
        this.mFileSize = mFileSize;
    }

    String getmFileName() {
        return mFileName;
    }

    void setmFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    String getmFileType() {
        return mFileType;
    }

    void setmFileType(String mFileType) {
        this.mFileType = mFileType;
    }

    String getmFileSize() {
        return mFileSize;
    }

    void setmFileSize(String mFileSize) {
        this.mFileSize = mFileSize;
    }

    public String getmFileDelete() {
        return mFileDelete;
    }

    public void setmFileDelete(String mFileDelete) {
        this.mFileDelete = mFileDelete;
    }

    String getmFileDownload() {
        return mFileDownload;
    }

    void setmFileDownload(String mFileDownload) {
        this.mFileDownload = mFileDownload;
    }
}
