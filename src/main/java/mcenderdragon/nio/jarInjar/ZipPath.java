package mcenderdragon.nio.jarInjar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.ImmutableDescriptor;

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
			if(getRoot() == this)
			{
				nameParts = new String[]{""};
				return nameParts;
			}
			else if(this.equals(getRoot()))
			{
				nameParts = new String[]{""};
				return nameParts;
			}
				
			
			nameParts = this.path.split(fs.getSeparator());
		}
		return nameParts;
	}

	@Override
	public Path getName(int index) 
	{
		return new ZipPath(fs, getNameParts()[index]);
	}

	@Override
	public Path subpath(int beginIndex, int endIndex) 
	{
		StringBuilder builder = new StringBuilder();
		for(int i=beginIndex;i<endIndex;i++)
		{
			builder.append(fs.getSeparator());
			builder.append(this.getNameParts()[i]);
		}
		return null;
	}

	@Override
	public boolean startsWith(Path other) 
	{
		if(other.getFileSystem()!=this.getFileSystem())
			return false;
		if(other instanceof ZipPath)
		{
			ZipPath otherP = (ZipPath) other;
			String[] parts = otherP.getNameParts();
			return startsWith(parts);
		}
		return false;
	}
	
	private boolean startsWith(String[] nameParts)
	{	
		if(nameParts.length > this.getNameParts().length)
		{
			return false;
		}
		for(int i=0;i<nameParts.length;i++)
		{
			if(!nameParts[i].equals(this.getNameParts()[i]))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean startsWith(String other) 
	{
		String[] parts = other.split(fs.getSeparator());
		return startsWith(parts);
	}

	@Override
	public boolean endsWith(Path other) 
	{
		if(other.getFileSystem()!=this.getFileSystem())
			return false;
		if(other instanceof ZipPath)
		{
			ZipPath otherP = (ZipPath) other;
			String[] parts = otherP.getNameParts();
			return endsWith(parts);
		}
		return false;
	}

	@Override
	public boolean endsWith(String other) 
	{
		String[] parts = other.split(fs.getSeparator());
		return endsWith(parts);
	}
	
	private boolean endsWith(String[] nameParts)
	{
		if(nameParts.length > this.getNameParts().length)
		{
			return false;
		}
		for(int i=0;i<nameParts.length;i++)
		{
			if(!nameParts[nameParts.length-i-1].equals(this.getNameParts()[this.getNameParts().length-i-1]))
			{
				return false;
			}
		}
		return true;
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
	public Path toRealPath(LinkOption... options) throws IOException 
	{
		return toAbsolutePath();
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
	public Iterator<Path> iterator() 
	{
		return new Iterator<Path>()
		{
			int i = 0;
			
			@Override
			public boolean hasNext() 
			{
				return i < getNameCount();
			}

			@Override
			public Path next() 
			{
				Path p = subpath(0, i+1);
				i++;
				return p;
			}
		};
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
		return this.fs.getBakedNode(this) != null;
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

	public ZFEntryAttribtes getAttributes() throws FileNotFoundException 
	{
		ZFEntryAttribtes attr = fs.getAttributes(this.toAbsolutePath());
		
		if(attr == null)
			throw new FileNotFoundException(this.toString());
		return attr;
	}

	public Map<String, Object> readAttributes(String attributes, LinkOption[] options) throws FileNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException 
	{
		String[] attrs;
		
		if(attributes.equals("*"))
		{
			attrs = new String[] {"lastModifiedTime", "lastAccessTime", "creationTime", "isRegularFile", "isDirectory", "isSymbolicLink", "isOther", "size", "fileKey"};
		}
		else if(attributes.contains(":"))
		{
			if(!attributes.startsWith("basic:"))
			{
				throw new UnsupportedOperationException("Only support basic file attributes");
			}
			else
			{
				attrs = attributes.replaceFirst("basic:", "").split(",");
			}
		}
		else
		{
			attrs = attributes.split(",");
		}
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		ZFEntryAttribtes zfe = getAttributes();
		Class<ZFEntryAttribtes> cls = ZFEntryAttribtes.class;
		
		for(String s : attrs)
		{
			map.put(s, cls.getMethod(s).invoke(zfe));
		}
		
		return map;
	}
}
