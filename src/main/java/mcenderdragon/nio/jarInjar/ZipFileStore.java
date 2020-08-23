package mcenderdragon.nio.jarInjar;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

public class ZipFileStore extends FileStore 
{

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String type() 
	{
		return "zip";
	}

	@Override
	public boolean isReadOnly() 
	{
		return true;
	}

	@Override
	public long getTotalSpace() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getUsableSpace() throws IOException 
	{
		return 0;
	}

	@Override
	public long getUnallocatedSpace() throws IOException 
	{
		return 0;
	}

	@Override
	public boolean supportsFileAttributeView(Class<? extends FileAttributeView> type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportsFileAttributeView(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(String attribute) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
