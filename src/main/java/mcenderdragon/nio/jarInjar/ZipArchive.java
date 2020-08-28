package mcenderdragon.nio.jarInjar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.text.Position;

public class ZipArchive implements Closeable, AutoCloseable
{
	public class ZippedFile implements Comparable<ZippedFile>
	{
		private final int position;
		private final ZipEntry entry;
		
		private SoftReference<byte[]> bytes;
		
		public ZippedFile(int position, ZipEntry entry) 
		{
			super();
			this.position = position;
			this.entry = entry;
		}
		
		@Override
		public int compareTo(ZippedFile o) 
		{
			return entry.getName().compareTo(o.entry.getName());
		}

		public String getPath() 
		{
			return entry.getName();
		}
		
		public ZFEntryAttribtes getAttributes()
		{
			return new ZFEntryAttribtes(entry);
		}
	}
	
	private Set<Closeable> toClose = Collections.synchronizedSet(new HashSet<Closeable>());
	
	private class ZipStream implements Closeable
	{
		private int currentEntryPos = -1;
		private ZipInputStream stream = null;
		private ZipEntry currentEntry = null;
		
		public void init(InputStream in) throws IOException
		{
			close();
			toClose.add(this);
			
			stream = new ZipInputStream(in);
			currentEntry = stream.getNextEntry();
			currentEntryPos = 0;
		}
		
		@Override
		public void close() throws IOException
		{
			currentEntryPos = -1;
			currentEntry = null;
			if(stream!=null)
				stream.close();
			stream = null;
			
			toClose.remove(this);
		}
		
		public byte[] getEntryData() throws IOException
		{
			int ss = (int) currentEntry.getSize();
			if(ss<0)
				ss = 1024;
			ByteArrayOutputStream bout = new ByteArrayOutputStream(ss);
			byte[] b = new byte[1024];
			while(stream.available() > 0)
			{
				int len = stream.read(b);
				if(len == -1)
					break;
				bout.write(b, 0, len);
			}
			next();
				
			return bout.toByteArray();
		}

		public void next() throws IOException 
		{
			stream.closeEntry();
			currentEntryPos++;
			currentEntry = stream.getNextEntry();
		}
	}
	
	private final byte[] rawData;
	private final BakeableTree.BakedNode<String, ZippedFile> fileTree;
	
	private final int fileCount;
	
	public static byte[] getContents(InputStream in) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
		byte[] buf = new byte[1024];
		while(in.available() > 0)
		{
			int wr = in.read(buf);
			out.write(buf, 0, wr);
		}
		in.close();
		out.close();
		return out.toByteArray();
	}
	
	public static ZipEntry[] getZipEntries(ZipInputStream zip) throws IOException
	{
		ArrayList<ZipEntry> list = new ArrayList<>();
		do
		{
			list.add(zip.getNextEntry());
		}
		while(list.get(list.size()-1) != null);
		if(list.get(list.size()-1) == null)
		{
			list.remove(list.size()-1);
		}
		
		return list.toArray(new ZipEntry[list.size()]);
	}
	
	public ZipArchive(InputStream in) throws IOException
	{
		this(getContents(in));
	}
	
	public ZipArchive(byte[] in) throws IOException
	{
		this.rawData = in;
		ByteArrayInputStream read = new ByteArrayInputStream(in);
		ZipInputStream zip = new ZipInputStream(read, StandardCharsets.UTF_8);
		ZipEntry[] entries = getZipEntries(zip);

		fileCount = entries.length;
		
		BakeableTree.RawNode<String,ZippedFile> nodes = new BakeableTree.RawNode<String,ZippedFile>(null, "");
		
		int positon = 0;
		for(ZipEntry e : entries)
		{
			ZippedFile zf = new ZippedFile(positon++, e);
			BakeableTree.addPath(e.getName(), "/", nodes).data = zf;
		}
		
		if(nodes.data == null)
		{
			ZipEntry entry = new ZipEntry("/");
			nodes.data = new ZippedFile(-1, entry);
		}
		
		fileTree = nodes.bake();		
		streamHolder = new ThreadLocal<ZipArchive.ZipStream>();
	}

	private ThreadLocal<ZipStream> streamHolder;
	
	protected byte[] getZipEntry(ZippedFile data) throws IOException
	{
		if(data.bytes!=null && data.bytes.get()!=null)
			return data.bytes.get();
		
		if(streamHolder.get()==null)
		{
			streamHolder.set(new ZipStream());
		}
		ZipStream zstream = streamHolder.get();
		if(zstream.stream==null)
		{
			zstream.init(new ByteArrayInputStream(rawData));	
		}
		if(zstream.stream!=null && zstream.currentEntryPos <= data.position)
		{
			while(zstream.currentEntryPos <= data.position)
			{
				if(zstream.currentEntry.getName().equals(data.entry.getName()))
				{
					byte[] d = zstream.getEntryData();
					data.bytes = new SoftReference<byte[]>(d); 
					return d;
				}
				else
				{
					BakeableTree.AbstractNode<String, ZippedFile> baked = BakeableTree.search(fileTree, ("/"+zstream.currentEntry.getName()).split("/"));
					if(baked!=null)
					{
						baked.data.bytes = new SoftReference<byte[]>(zstream.getEntryData());
					}
					else
					{
						zstream.next();
					}
					
				}
			}
		}
		
		zstream.close();
		return getZipEntry(data);
	}


	@Override
	public void close() throws IOException 
	{
		toClose.forEach(t -> 
		{
			try 
			{
				t.close();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		});
	}

	public BakeableTree.BakedNode<String, ZippedFile> getPathNode(String[] path) 
	{
		if(path.length==0)
			throw new IllegalArgumentException("Emtpy string array is not allowed");
		
		BakeableTree.AbstractNode<String, ZippedFile> n = BakeableTree.search(fileTree, path);
		if(n==null)
		{
			n = BakeableTree.search(fileTree, path);
		}
		return n.bake();
	}

	public SeekableByteChannel newByteChannel(ZippedFile data) throws IOException 
	{
		return new ByteArraySBC(getZipEntry(data));
	}
	
	
}
