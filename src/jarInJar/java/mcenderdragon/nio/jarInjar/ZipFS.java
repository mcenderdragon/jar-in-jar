package mcenderdragon.nio.jarInjar;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.management.ImmutableDescriptor;

public class ZipFS extends AbstractReadOnlyFileSystem<ZipFSProvider>
{
	private Path zip;
	private final ZipPath root;
	
	public ZipFS(ZipFSProvider provider, Path absolute) 
	{
		super(provider);
		this.zip = absolute;
		this.root = new ZipPath(this, "/");
	}

	@Override
	public String getSeparator() 
	{
		return "/";
	}

	@Override
	public Iterable<Path> getRootDirectories() 
	{
		return Collections.singleton(getRoot());
	}

	@Override
	public Iterable<FileStore> getFileStores() {
		// TODO Auto-generated method stub
		return null;
	}

	private static final Set<String> supportedFileAttributeViews = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("basic", "zip")));
			     
	@Override
	public Set<String> supportedFileAttributeViews() 
	{
		return supportedFileAttributeViews;
	}

	@Override
	public Path getPath(String first, String... more) 
	{
		String path;
		if(more.length==0)
		{
			path = first;
		}
		else
		{
			StringBuilder build = new StringBuilder(first);
			for(String s : more)
			{
				if(s.length() > 0)
				{
					if(build.length() > 0)
						build.append(getSeparator());
					build.append(s);
				}
			}
			path = build.toString();
		}
		return new ZipPath(this, path);
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService() 
	{
		throw new UnsupportedOperationException();
	}

	public Path getRoot() 
	{
		return root;
	}

	public Path getZipPath() 
	{
		return zip;
	}
	
	@Override
	public void close() throws IOException 
	{
		// TODO Auto-generated method stub
		super.close();
	}

}
