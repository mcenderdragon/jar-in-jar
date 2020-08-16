package mcenderdragon.nio.jarInjar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public class ZipPath extends AbstractPath<ZipFS>
{
	private ZipPath parent = null;
	private final boolean isAbsolute;
	private int nameCount = -1;
	
	private String[] nameParts = null;
	
	public ZipPath(ZipFS fs, String path) 
	{
		super(fs, path);
		isAbsolute = path.equals(toAbsolutePath(path, fs.getSeparator()));
	}

	@Override
	public Path getRoot() 
	{
		return fs.getRoot();
	}

	@Override
	public Path getFileName() 
	{
		if(path.isEmpty())
			return null;
		if(getRoot() == this)
		{
			return this;
		}
		else
		{
			int e = path.lastIndexOf(fs.getSeparator());
			if(e==-1)
				return this;
			else
			{
				return new ZipPath(fs, path.substring(e+1));
			}
		}
	}

	@Override
	public Path getParent() 
	{
		if(parent==null)
		{
			if(getRoot() == this)
			{
				parent = this;
			}
			else
			{
				int pos = path.lastIndexOf(fs.getSeparator());
				parent = new ZipPath(fs, path.substring(0, pos));
			}
		}
		return parent;
	}

	@Override
	public int getNameCount() 
	{
		return getNameParts().length;
	}
	
	public String[] getNameParts()
	{
		if(nameParts == null)
		{
			String[] parts = this.path.split(fs.getSeparator());
			int i;
			for(i=0;i<parts.length;i++)
			{
				if(!parts[i].isEmpty())
					break;
			}
			nameParts = new String[parts.length-i];
			System.arraycopy(parts, i, nameParts, 0, nameParts.length);
		}
		return nameParts;
	}

	@Override
	public Path getName(int index) 
	{
		return new ZipPath(fs, getNameParts()[index]);
	}

	@Override
	public Path subpath(int beginIndex, int endIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean startsWith(Path other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean startsWith(String other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean endsWith(Path other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean endsWith(String other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Path normalize() 
	{
		return toAbsolutePath();
	}

	@Override
	public Path resolve(Path other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path resolve(String other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path resolveSibling(Path other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path resolveSibling(String other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path relativize(Path other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI toUri() 
	{
		try 
		{
			return new URI(fs.provider().getScheme(), path + "?" + fs.getZipPath().toUri().toString(), "");
		}
		catch (URISyntaxException e) 
		{
			throw new IllegalStateException(e);
		}
	}

	@Override
	public ZipPath toAbsolutePath() 
	{
		if(isAbsolute())
		{
			return this;
		}
		return new ZipPath(fs, toAbsolutePath(path, fs.getSeparator()));
	}

	@Override
	public Path toRealPath(LinkOption... options) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File toFile() 
	{
		throw new UnsupportedOperationException("This file is still in a Zip!");
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers) throws IOException 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>... events) throws IOException 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Path> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isHidden() 
	{
		return false;
	}

	public void checkAccess(AccessMode[] modes) throws IOException
	{
		boolean r = false;
		for(AccessMode m : modes)
		{
			switch(m)
			{
			case READ:
			case EXECUTE:
				r = true;
				break;
			case WRITE:
				default:
				throw new UnsupportedOperationException();
			}
		}
		
		if(r)
		{
			if(!this.exists())
			{
				throw new FileNotFoundException(this.path);
			}
		}
		
	}

	public boolean exists() 
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSameFile(Path path2) 
	{
		if(this.equals(path2))
			return true;
		else if(path2 == null || path2.getFileSystem() != this.fs)
			return false;
		else
		{
			return false; 
		}
	}

	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		else if(obj == null)
			return false;
		else if(obj instanceof ZipPath)
		{
			return ((ZipPath)obj).fs == this.fs && ((ZipPath)obj).path.equals(this.path);
		}
		else
			return false;
	}

	public DirectoryStream<Path> newDirectoryStream(Filter<? super Path> filter) 
	{
		return new ZipFSDirStream(this, filter);
	}

	public SeekableByteChannel newByteChannel() throws IOException 
	{
		return fs.newByteChannel(this);
	}
}
