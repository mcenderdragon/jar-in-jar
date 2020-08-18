package mcenderdragon.nio.jarInjar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.management.ImmutableDescriptor;

import mcenderdragon.nio.jarInjar.BakeableTree.BakedNode;
import mcenderdragon.nio.jarInjar.ZipArchive.ZippedFile;

public class ZipFS extends AbstractReadOnlyFileSystem<ZipFSProvider>
{
	private Path zip;
	private final ZipPath root;
	
	private final ZipArchive archive;
	
	public ZipFS(ZipFSProvider provider, Path absolute) throws IOException 
	{
		super(provider);
		this.zip = absolute;
		this.root = new ZipPath(this, "/");
		
		archive = new ZipArchive(Files.newInputStream(absolute, StandardOpenOption.READ));
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
		super.close();
		archive.close();
		
		provider.close(this);
	}

	public BakeableTree.BakedNode<String, ZipArchive.ZippedFile> getBakedNode(ZipPath rootDir) 
	{
		if(rootDir.getFileSystem() != this)
			throw new IllegalArgumentException("Filesystem on Path is different to this!");

		return archive.getPathNode(rootDir.getNameParts());
	}

	public SeekableByteChannel newByteChannel(ZipPath zipPath)  throws IOException
	{
		 BakeableTree.BakedNode<String, ZipArchive.ZippedFile> node = getBakedNode(zipPath);
		 if(node == null)
		 {
			 throw new FileNotFoundException(zipPath.toString());
		 }
		 
		return archive.newByteChannel(node.data);
	}

	public ZFEntryAttribtes getAttributes(ZipPath absolutePath) 
	{
		BakeableTree.BakedNode<String, ZipArchive.ZippedFile> node = getBakedNode(absolutePath);
		if(node == null)
			return null;
		
		return node.data.getAttributes();
	}

}
