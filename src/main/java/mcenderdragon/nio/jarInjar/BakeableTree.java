package mcenderdragon.nio.jarInjar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import mcenderdragon.nio.jarInjar.ZipArchive.ZippedFile;

public class BakeableTree 
{
	
	public static <R> AbstractNode<String,R> addPath(String path, String separator, RawNode<String,R> root)
	{
		return addPath(path.split(separator), root);
	}
	
	public static <R> AbstractNode<String,R> addPath(String[] path, RawNode<String,R> root)
	{
		AbstractNode<String,R> n = root;
		for(String s : path)
		{
			if(n==root && s.equals(root.key))
				continue;
			n = n.addIfAbsend(s, null);
		}
		return n;
	}
	
	public static <T extends Comparable<T>> void printTree(int startDepth, AbstractNode<T,?> node, char seperator)
	{
		char[] cc = new char[startDepth];
		Arrays.fill(cc, seperator);
		String s = new String(cc);
//		System.out.println(s + node.key);
		for(AbstractNode<T,?> n : node.getChildren())
		{
			printTree(startDepth+1, n, seperator);
		}
	}
	
	public static <T extends Comparable<T>,R> AbstractNode<T, R> search(AbstractNode<T, R> node, T[] path) 
	{
		int pos = 0;
		if(node.key.equals(path[pos]))
		{
			pos++;
			while(node != null && pos < path.length)
			{
				AbstractNode<T, R> child = node.getNode(path[pos]);
				if(child != null)
				{
					node = child;
					pos++;
				}
				else
				{
					return null;
				}
			}
			
			if(pos == path.length)
			{
				return node;
			}
		}
		return null;
	}
	
	public static abstract class AbstractNode<T extends Comparable<T>, R> implements Comparable<AbstractNode<T,R>>
	{
		T key;
		public R data;
		AbstractNode<T,R> parent;
		
		public AbstractNode(AbstractNode<T,R> parent,T t) 
		{
			key = t;
			this.parent = parent;
		}
		
		public AbstractNode<T,R> getRoot()
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
		
		public AbstractNode<T,R> getParent()
		{
			return parent;
		}
		
		public abstract boolean isRaw();
		
		public abstract boolean isBacked();
		
		public abstract BakedNode<T,R> bake();
		
		public abstract List<? extends AbstractNode<T,R>> getChildren();
		
		public abstract AbstractNode<T,R> addChildren(T key, R data);
		
		public boolean isLeave()
		{
			return getChildren().isEmpty();
		}
		
		public AbstractNode<T,R> addIfAbsend(T value, R data)
		{
			AbstractNode<T,R> n = getNode(value);
			return n != null ? n : addChildren(value, data);
		}
		
		public AbstractNode<T,R> getNode(T key)
		{
			List<? extends AbstractNode<T,R>> l = getChildren();
			
			int start = 0;
			int length = l.size() / 2;
			for(int j=0;j<l.size();j++)
			{
				int index = start + length;
				AbstractNode<T,R> n = l.get(index);
				if(n.key.equals(key))
				{
					return n;
				}
				else
				{
					if(key.compareTo(n.key) < 0)
					{
						length = length / 2;
					}
					else
					{
						start = index;
						if(start+length>=l.size())
							length = l.size() - start-1;
//						length = length / 2;
					}
				}
			}
			return null;
		}
		
		@Override
		public int compareTo(AbstractNode<T,R> o) 
		{
			return key.compareTo(o.key);
		}
		
		@Override
		public String toString() 
		{
			return getParent() + " -> " + key;
		}
	}
	
	public static class RawNode<T extends Comparable<T>,R> extends AbstractNode<T,R>
	{
		private ArrayList<RawNode<T,R>> list = new ArrayList<RawNode<T,R>>();
		
		public RawNode(RawNode<T,R> parent, T t) 
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
		public BakedNode<T,R> bake() 
		{
			BakedNode[] baked = new BakedNode[list.size()];
			for(int i=0;i<baked.length;i++)
			{
				baked[i] = list.get(i).bake();
			}
			BakedNode bn = new BakedNode(null, this.key, baked);
			bn.data = this.data;
			return bn;
		}

		@Override
		public List<RawNode<T,R>> getChildren() 
		{
			return list;
		}

		@Override
		public RawNode<T,R> addChildren(T child, R data) 
		{
			RawNode n = new RawNode<T,R>(this, child);
			list.add(n);
			Collections.sort(list);
			return n;
		}
	}
	
	public static class BakedNode<T extends Comparable<T>,R> extends AbstractNode<T,R>
	{
		final BakedNode[] children;
		private List<AbstractNode<T,R>> list = null;
		
		public BakedNode(BakedNode<T,R> parent, T t, BakedNode[] children) 
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
		public BakedNode<T,R> bake() 
		{
			return this;
		}

		@Override
		public List<AbstractNode<T,R>> getChildren() 
		{
			if(list==null)
				list = isLeave() ? Collections.EMPTY_LIST : Arrays.asList(children);
			return list;
		}

		@Override
		public AbstractNode<T,R> addChildren(T child, R data) 
		{
			throw new UnsupportedOperationException("Already Backed");
		}

		@Override
		public boolean isLeave() 
		{
			return children == null;
		}
	}
	
	public static void main(String[] args) 
	{
		RawNode<String,?> root = new RawNode(null, "");
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
		BakedNode<String,?> bn = root.bake();
		s = System.nanoTime();
		printTree(0, bn, '-');
		System.out.println((System.nanoTime() - s) + "ns");
	}

	public static void compareTimes(RawNode<String,?> nodes, BakedNode<String,?> fileTree) 
	{
		int count = 1000;
		long s1=0,s2=0;
		for(int i=0;i<count;i++)
		{
			long s = System.nanoTime();
			printTree(0, nodes, '-');
			s1 += System.nanoTime() - s;
			
			s = System.nanoTime();
			printTree(0, fileTree, '-');
			s2 += System.nanoTime() - s;
		}
		double ns1 = (double) s1 / (double) count;
		double ns2 = (double) s2 / (double) count;
		
		System.out.println("Raw");
		System.out.println(ns1 + "ns");
		System.out.println("Baked");
		System.out.println(ns2 + "ns");
		
	}
}
