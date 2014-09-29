package kdkbot.filemanager;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	private Path filePath;
	private String curDate = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
	
	public Log() {
		filePath = FileSystems.getDefault().getPath("./logs/" + curDate + ".log");
	}
}
