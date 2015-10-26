package au.com.tyo.io;

import java.io.File;

public interface CacheInterface<FileType> {
	
	FileType read(File file) throws Exception;
	
	void write(FileType target, File file) throws Exception;
}
