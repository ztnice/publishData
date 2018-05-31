package sql.dbdo;

public class HzReleaseFileRecord {
	private String recordCode;

	private String fileName;

	private String filePath;

	private String itemId;

	private String itemrevision;
	private String fileType;

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getRecordCode() {
		return recordCode;
	}

	public void setRecordCode(String recordCode) {
		this.recordCode = recordCode == null ? null : recordCode.trim();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName == null ? null : fileName.trim();
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath == null ? null : filePath.trim();
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId == null ? null : itemId.trim();
	}

	public String getItemrevision() {
		return itemrevision;
	}

	public void setItemrevision(String itemrevision) {
		this.itemrevision = itemrevision == null ? null : itemrevision.trim();
	}
}