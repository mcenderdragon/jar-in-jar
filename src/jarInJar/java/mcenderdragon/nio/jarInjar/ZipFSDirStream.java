package mcenderdragon.nio.jarInjar;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import mcenderdragon.nio.jarInjar.BakeableTree.AbstractNode;
import mcenderdragon.nio.jarInjar.ZipArchive.ZippedFile;

public class ZipFSDirStream implements DirectoryStream<Path>
{
	private final ZipPath rootDir;
	private final Filter<? super Path> filter;
	private final BakeableTree.BakedNode<String, ZipArchive.ZippedFile> node;
	
	public ZipFSDirStream(ZipPath rootDir, Filter<? super Path> filter) 
	{
		super();
		this.rootDir = rootDir;
		this.filter = filter;
		
		node = rootDir.getFileSystem().getBakedNode(rootDir.toAbsolutePath());
	}

	@Override
	public void close() throws IOException 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<Path> iterator() 
	{
		return new Iter(rootDir.getFileSystem(), node, filter);
	}

	public static class Iter implements Iterator<Path>
	{
		private final ZipFS fs;
		private final BakeableTree.BakedNode<String, ZipArchive.ZippedFile> node;
		private final Filter<? super Path> filter;
		
		private ZipPath next;
		private boolean end = false;
		
		public Iter(ZipFS fs, BakeableTree.BakedNode<String, ZipArchive.ZippedFile> node, Filter<? super Path> filter) 
		{
			super();
			this.fs = fs;
			this.node = node;
			this.filter = filter;
		}

		@Override
		public boolean hasNext() 
		{
			computeNext();
			return !end;
		}

		@Override
		public ZipPath next() 
		{
			ZipPath n = computeNext();
			next = null;
			return n;
		}
		
		private void stop()
		{
			end = true;
		}
		
		private ZipPath computeNext()
		{
			try
			{
				if(next!=null)
					return next;
				else
				{
					while(!end && next==null)
					{
						next = computeNextImp();
						
						if(!filter.accept(next))
						{
							next = null;
						}
					}
					if(next==null)
						end = true;
					return next;
				}
			}
			catch (IOException e) 
			{
				throw new DirectoryIteratorException(e);
			}
		}
		
		private Stack<BakeableTree.BakedNode<String, ZipArchive.ZippedFile>> nodes;
		private Stack<Integer> positions;
		
		private ZipPath computeNextImp()
		{
			ZipArchive.ZippedFile file = computeNextImpBase();
			
			return new ZipPath(fs, file.getPath());
		}
		
		private ZipArchive.ZippedFile computeNextImpBase()
		{
			if(nodes==null && positions == null)
			{
				nodes = new Stack<>();
				positions = new Stack<>();
				nodes.push(node);
				positions.push(0);
				
				return node.data;
			}
			
			if(nodes.isEmpty())
			{
				stop();
				return null;
			}
			
			BakeableTree.BakedNode<String, ZipArchive.ZippedFile> active = nodes.peek();
			int pos = positions.peek();
			
			List<AbstractNode<String, ZippedFile>> childs = active.getChildren();
			if(pos < childs.size())
			{
				BakeableTree.BakedNode<String, ZipArchive.ZippedFile> child = childs.get(pos).bake();
				positions.push(0);
				nodes.push(child);
				return child.data;
			}
			else
			{
				nodes.pop();
				positions.pop();
				return computeNextImpBase();
			}
		}
	}
}
