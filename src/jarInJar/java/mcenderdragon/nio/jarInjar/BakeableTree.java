package mcenderdragon.nio.jarInjar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class BakeableTree 
{
	
	public static void addPath(String path, String separator, RawNode<String> root)
	{
		addPath(path.split(separator), root);
	}
	
	public static void addPath(String[] path, RawNode<String> root)
	{
		AbstractNode<String> n = root;
		for(String s : path)
		{
			if(n==root && s.equals(root.data))
				continue;
			n = n.addIfAbsend(s);
		}
	}
	
	public static <T extends Comparable<T>> void printTree(int startDepth, AbstractNode<T> node, char seperator)
	{
		char[] cc = new char[startDepth];
		Arrays.fill(cc, seperator);
		String s = new String(cc);
		System.out.println(s + node.data);
		for(AbstractNode<T> n : node.getChildren())
		{
			printTree(startDepth+1, n, seperator);
		}
	}
	
	public static abstract class AbstractNode<T extends Comparable<T>> implements Comparable<AbstractNode<T>>
	{
		T data;
		AbstractNode<T> parent;
		
		public AbstractNode(AbstractNode<T> parent,T t) 
		{
			data = t;
			this.parent = parent;
		}
		
		public AbstractNode<T> getRoot()
		{
			if(getParent()!=null)
			{
				return parent.getRoot();
			}
			else
			{
				return this;
			}
		}
		
		public AbstractNode<T> getParent()
		{
			return parent;
		}
		
		public abstract boolean isRaw();
		
		public abstract boolean isBacked();
		
		public abstract BakedNode<T> bake();
		
		public abstract List<? extends AbstractNode<T>> getChildren();
		
		public abstract AbstractNode<T> addChildren(T child);
		
		public boolean isLeave()
		{
			return getChildren().isEmpty();
		}
		
		public AbstractNode<T> addIfAbsend(T value)
		{
			AbstractNode<T> n = getNode(value);
			return n != null ? n : addChildren(value);
		}
		
		public AbstractNode<T> getNode(T value)
		{
			List<? extends AbstractNode<T>> l = getChildren();
			
			int start = 0;
			int offset = l.size() / 2;
			for(int j=0;j<l.size();j++)
			{
				int index = start + offset;
				AbstractNode<T> n = l.get(index);
				if(n.data.equals(value))
				{
					return n;
				}
				else
				{
					if(value.compareTo(n.data) < 0)
					{
						offset = offset / 2;
					}
					else
					{
						start = index;
						offset = offset / 2;
					}
				}
			}
			return null;
		}
		
		@Override
		public int compareTo(AbstractNode<T> o) 
		{
			return data.compareTo(o.data);
		}
		
		@Override
		public String toString() 
		{
			return getParent() + " -> " + data;
		}
	}
	
	public static class RawNode<T extends Comparable<T>> extends AbstractNode<T>
	{
		private ArrayList<RawNode<T>> list = new ArrayList<RawNode<T>>();
		
		public RawNode(RawNode<T> parent, T t) 
		{
			super(parent, t);
		}

		@Override
		public boolean isRaw() 
		{
			return true;
		}

		@Override
		public boolean isBacked() 
		{
			return false;
		}

		@Override
		public BakedNode<T> bake() 
		{
			BakedNode[] baked = new BakedNode[list.size()];
			for(int i=0;i<baked.length;i++)
			{
				baked[i] = list.get(i).bake();
			}
			return new BakedNode(null, this.data, baked);
		}

		@Override
		public List<RawNode<T>> getChildren() 
		{
			return list;
		}

		@Override
		public RawNode<T> addChildren(T child) 
		{
			RawNode n = new RawNode<T>(this, child);
			list.add(n);
			Collections.sort(list);
			return n;
		}
	}
	
	public static class BakedNode<T extends Comparable<T>> extends AbstractNode<T>
	{
		final BakedNode[] children;
		
		public BakedNode(BakedNode<T> parent, T t, BakedNode[] children) 
		{
			super(parent, t);
			if(children==null || children.length==0)
				this.children = null;
			else
			{
				this.children = children;
				for(BakedNode n : children)
				{
					n.parent = this;
				}
			}
		}

		@Override
		public boolean isRaw() 
		{
			return false;
		}

		@Override
		public boolean isBacked() 
		{
			return true;
		}

		@Override
		public BakedNode<T> bake() 
		{
			return this;
		}

		@Override
		public List<AbstractNode<T>> getChildren() 
		{
			return isLeave() ? Collections.EMPTY_LIST : Arrays.asList(children);
		}

		@Override
		public AbstractNode<T> addChildren(T child) 
		{
			throw new UnsupportedOperationException("ALreadyBacked");
		}

		@Override
		public boolean isLeave() 
		{
			return children == null;
		}
	}
	
	public static void main(String[] args) 
	{
		RawNode<String> root = new RawNode<String>(null, "");
		addPath("/test/foo/barr.txt", "/", root);
		addPath("/test/foo/barr", "/", root);
		addPath("/test/foo/barr.tx", "/", root);
		addPath("/test/foo/", "/", root);
		addPath("/test/", "/", root);
		addPath("/test", "/", root);
		addPath("/a", "/", root);
		addPath("/b", "/", root);
		addPath("/bb", "/", root);
		addPath("/aa", "/", root);
		addPath("/ab", "/", root);
		addPath("/ac", "/", root);
		addPath("/c", "/", root);
		addPath("/bb/b", "/", root);
		addPath("/bb/b/b", "/", root);
		addPath("/bb/b/b/b", "/", root);
		addPath("/bb/b/b/b/b", "/", root);
		addPath("/bb/b/b/b/b/b", "/", root);
		
		System.out.println("Raw");
		long s = System.nanoTime();
		printTree(0, root, '-');
		System.out.println((System.nanoTime() - s) + "ns");
		System.out.println("Baked");
		BakedNode<String> bn = root.bake();
		s = System.nanoTime();
		printTree(0, bn, '-');
		System.out.println((System.nanoTime() - s) + "ns");
	}

	public static void compareTimes(RawNode<String> nodes, BakedNode<String> fileTree) 
	{

		System.out.println("Raw");
		long s = System.nanoTime();
		printTree(0, nodes, '-');
		long s1 = System.nanoTime() - s;
		
		System.out.println("Baked");
		s = System.nanoTime();
		printTree(0, fileTree, '-');
		System.out.println((s1) + "ns");
		System.out.println((System.nanoTime() - s) + "ns");
	}
}
