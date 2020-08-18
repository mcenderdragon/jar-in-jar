package mcenderdragon.nio.jarInjar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.ProviderMismatchException;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ZipFSProvider extends AbstractReadOnlyFileSystemProvider 
{
	
	private Map<Path, ZipFS> filesystems = new HashMap<Path, ZipFS>();

	@Override
	public String getScheme() 
	{
		return "jarInJar";
	}

	
	protected Path getPathToZip(URI uri)
	{
		if(uri.getScheme() == null || !uri.getScheme().equalsIgnoreCase(getScheme()))
			throw new IllegalArgumentException("URI scheme is not '" + getScheme() + "'");
		
		if(uri.getQuery() == null || uri.getQuery().length()==0)
			throw new IllegalArgumentException("Query must contain path to zip like " + getScheme()+":/path/in/zip?URI_TO_ZIP");	
		
		
		String oldUri = uri.getQuery();
			
		try 
		{
			return Paths.get(new URI(oldUri));
		}
		catch (URISyntaxException e)  {
			throw new IllegalArgumentException(e.getMessage(), e);	
		}
	}
	
	@Override
	public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException 
	{
		return newFileSystem(getPathToZip(uri), env);
	}
	
	public FileSystem newFileSystem(Path path, Map<String,?> env)  throws IOException 
	{
		Path absolute = path.toAbsolutePath();
		
		synchronized (filesystems) 
		{
			if(filesystems.containsKey(absolute))
			{
				throw new FileSystemAlreadyExistsException();
			}
			else
			{
				ZipFS fs = new ZipFS(this, path);
				
				filesystems.put(absolute, fs);
				
				return fs;
			}
		}
		
	}

	@Override
	public FileSystem getFileSystem(URI uri) 
	{
		return getFileSystem(getPathToZip(uri));
	}

	private FileSystem getFileSystem(Path pathToZip)
	{
		Path absolute = pathToZip.toAbsolutePath();
		synchronized (filesystems) 
		{
			ZipFS fs = filesystems.get(absolute);
			if( fs == null)
			{
				throw new FileSystemNotFoundException();
			}
			else
			{
				return fs;
			}
		}
	}
	
	@Override
	public Path getPath(URI uri) 
	{
		Path pathToZip = getPathToZip(uri);
		return getFileSystem(pathToZip).getPath(uri.getPath());
	}

	protected ZipPath toZipPath(Path path)
	{
		 if (path == null)
			 throw new NullPointerException();
		 if (!(path instanceof ZipPath))
			 throw new ProviderMismatchException();
		return (ZipPath) path;
	}
	
	@Override
	public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException 
	{
		if(attrs.length != 0)
		{
			throw new UnsupportedOperationException("FS is readonly");
		}
		for(OpenOption opt : options)
		{
			if(opt != StandardOpenOption.READ)
			{
				throw new UnsupportedOperationException("Only Read is currently supported");
			}
		}
		
		return  toZipPath(path).newByteChannel();
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter) throws IOException 
	{
		return toZipPath(dir).newDirectoryStream(filter);
	}

	@Override
	public boolean isSameFile(Path path, Path path2) throws IOException 
	{
		return toZipPath(path).isSameFile(path2);
	}

	@Override
	public boolean isHidden(Path path) throws IOException 
	{
		return toZipPath(path).isHidden();
	}

	@Override
	public FileStore getFileStore(Path path) throws IOException 
	{
		return null;
	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) throws IOException 
	{
		toZipPath(path).checkAccess(modes);
	}

	@Override
	public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) 
	{
		try 
		{
			return (V) new ZEEntryAttributesView(toZipPath(path).getAttributes());
		}
		catch (FileNotFoundException e) 
		{
			throw new IllegalStateException(e);
		}
	}

	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException 
	{
		if(type == BasicFileAttributes.class || type == ZFEntryAttribtes.class)
			return (A) toZipPath(path).getAttributes();
		
		return null;
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException 
	{
		try 
		{
			return toZipPath(path).readAttributes(attributes, options);
		} 
		catch (IllegalArgumentException | SecurityException | ReflectiveOperationException e) 
		{
			throw new UnsupportedOperationException(e);
		}
	}


	public void close(ZipFS zipFS) 
	{
		synchronized (filesystems) 
		{
			filesystems.remove(zipFS.getZipPath());
		}
	}

}
