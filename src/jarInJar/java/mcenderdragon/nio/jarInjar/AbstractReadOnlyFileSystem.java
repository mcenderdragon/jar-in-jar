package mcenderdragon.nio.jarInjar;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;

public abstract class AbstractReadOnlyFileSystem extends FileSystem 
{
	private final FileSystemProvider provider;
	private boolean open = true;
	
	public AbstractReadOnlyFileSystem(FileSystemProvider provider) 
	{
		this.provider = provider;
	}

	@Override
	public FileSystemProvider provider() 
	{
		return provider;
	}

	@Override
	public void close() throws IOException 
	{
		open = false;
	}

	@Override
	public boolean isOpen() 
	{
		return open;
	}

	@Override
	public boolean isReadOnly() 
	{
		return true;
	}
}
