package mcenderdragon.nio.jarInjar;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;

public abstract class AbstractReadOnlyFileSystemProvider extends FileSystemProvider 
{
	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException 
	{
		throw new ReadOnlyFileSystemException();
	}

	@Override
	public void delete(Path path) throws IOException 
	{
		throw new ReadOnlyFileSystemException();
	}

	@Override
	public void copy(Path source, Path target, CopyOption... options) throws IOException 
	{
		throw new ReadOnlyFileSystemException();
	}

	@Override
	public void move(Path source, Path target, CopyOption... options) throws IOException 
	{
		throw new ReadOnlyFileSystemException();
	}

	@Override
	public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException 
	{
		throw new ReadOnlyFileSystemException();
	}

}
