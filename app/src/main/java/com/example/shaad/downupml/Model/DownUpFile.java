package com.example.shaad.downupml.Model;

public class DownUpFile {
    private String mFileName;
    private String mFileType;
    private String mFileSize;
    private String mFileDelete;
    private String mFileDownload;

    DownUpFile() {
    }

    @Override
    public String toString() {
        return "DownUpFile{" +
                "mFileName='" + mFileName + '\'' +
                ", mFileType='" + mFileType + '\'' +
                ", mFileSize='" + mFileSize + '\'' +
                ", mFileDelete='" + mFileDelete + '\'' +
                ", mFileDownload='" + mFileDownload + '\'' +
                '}';
    }

    public DownUpFile(String mFileName, String mFileType, String mFileSize, String mFileDownload) {
        this.mFileName = mFileName;
        this.mFileType = mFileType;
        this.mFileSize = mFileSize;
        this.mFileDownload = mFileDownload;
    }

    public String getFileName() {
        return mFileName;
    }

    void setFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public String getFileType() {
        return mFileType;
    }

    void setFileType(String mFileType) {
        this.mFileType = mFileType;
    }

    public String getFileSize() {
        return mFileSize;
    }

    void setFileSize(String mFileSize) {
        this.mFileSize = mFileSize;
    }

    public String getFileDelete() {
        return mFileDelete;
    }

    public void setFileDelete(String mFileDelete) {
        this.mFileDelete = mFileDelete;
    }

    public String getFileDownload() {
        return mFileDownload;
    }

    void setFileDownload(String mFileDownload) {
        this.mFileDownload = mFileDownload;
    }
}
