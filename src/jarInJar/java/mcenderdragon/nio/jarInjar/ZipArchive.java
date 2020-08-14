package mcenderdragon.nio.jarInjar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.text.Position;

public class ZipArchive 
{
	private final byte[] rawData;
	private final TreeMap<String, ZippedFile> name2entry;
	
	private final BakeableTree.BakedNode<String> fileTree;
	
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
		ArrayList<ZipEntry> list = new ArrayList();
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
		
		name2entry = new TreeMap<String, ZippedFile>();
		
		BakeableTree.RawNode<String> nodes = new BakeableTree.RawNode<String>(null, "");
		
		int positon = 0;
		for(ZipEntry e : entries)
		{
			System.out.println( positon + " " + e.getName());
			ZippedFile zf = new ZippedFile(positon++, e);
			BakeableTree.addPath(e.getName(), "/", nodes);
			name2entry.put(e.getName(), zf);
			
		}
		
		fileTree = nodes.bake();
		BakeableTree.compareTimes(nodes, fileTree);
	}
	
	
	private class ZippedFile implements Comparable<ZippedFile>
	{
		private final int position;
		private final ZipEntry entry;
		
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
		
		
	}
	
	
}
