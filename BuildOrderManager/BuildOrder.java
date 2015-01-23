package BuildOrderManager;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import bwapi.UnitType;

public class BuildOrder implements Queue<BuildOrderItem>{
	private LinkedList<BuildOrderItem> queue = new LinkedList<BuildOrderItem>();
	
	public void queueWorkers(int number){
		for(int i = 0; i < number; i++){
			queue.add(new BuildOrderItem(UnitType.Protoss_Probe));
		}
	}

	@Override
	public boolean addAll(Collection<? extends BuildOrderItem> c) {
		for(BuildOrderItem buildOrderItem : c){
			queue.add(buildOrderItem);
		}
		return true;
	}

	@Override
	public void clear() {
		queue = new LinkedList<BuildOrderItem>();
	}

	@Override
	public boolean contains(Object o) {
		return queue.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return queue.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public Iterator<BuildOrderItem> iterator() {
		return queue.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return queue.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return queue.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return queue.retainAll(c);
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public Object[] toArray() {
		return queue.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return queue.toArray(a);
	}

	@Override
	public boolean add(BuildOrderItem e) {
		return queue.add(e);
	}

	@Override
	public BuildOrderItem element() {
		return queue.element();
	}

	@Override
	public boolean offer(BuildOrderItem e) {
		return queue.offer(e);
	}

	@Override
	public BuildOrderItem peek() {
		return queue.peek();
	}

	@Override
	public BuildOrderItem poll() {
		return queue.poll();
	}

	@Override
	public BuildOrderItem remove() {
		return queue.remove();
	}	
}
