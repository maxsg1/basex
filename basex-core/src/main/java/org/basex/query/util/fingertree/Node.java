package org.basex.query.util.fingertree;

import java.util.*;

/**
 * A node inside a digit.
 *
 * @author BaseX Team 2005-15, BSD License
 * @author Leo Woerteler
 *
 * @param <N> node type
 * @param <E> element type
 */
public abstract class Node<N, E> extends NodeLike<N, E> implements Iterable<E> {
  /**
   * Number of elements in this node.
   * @return number of elements
   */
  protected abstract long size();

  /**
   * Number of children of this node.
   * @return number of children
   */
  protected abstract int arity();

  /**
   * Returns the sub-node at the given position in this node.
   * @param pos index of the sub-node, must be between 0 and {@link #arity()} - 1
   * @return the sub-node
   */
  protected abstract N getSub(final int pos);

  /**
   * Creates a reversed version of this node.
   * @return a node with the reverse order of contained elements
   */
  protected abstract Node<N, E> reverse();

  /**
   * Inserts the given element at the given position in this node.
   * The array {@code siblings} is used for input as well as output. It must contain the left and
   * right sibling of this node (if existing) at positions 0 and 2 when calling the method.
   * After the method returns, there are two cases to consider:
   * <ul>
   *   <li>
   *     If the method returned {@code true} (i.e. this node was split), the array contains the
   *     left sibling at position 0, the split node at position 1 and 2 and the right sibling at 3.
   *   </li>
   *   <li>
   *     Otherwise the array contains (possibly modified versions of) left sibling at position 0,
   *     this node at position 1 and the right sibling at position 2.
   *   </li>
   * </ul>
   * @param siblings sibling array for input and output
   * @param pos insertion position
   * @param val value to insert
   * @return {@code true} if the node was split, {@code false} otherwise
   */
  protected abstract boolean insert(Node<N, E>[] siblings, final long pos, final E val);

  /**
   * Removes the element at the given position in this node.
   * @param pos position of the element to remove
   * @return possibly partial resulting node
   */
  protected abstract NodeLike<N, E> remove(final long pos);

  /**
   * Removes the element at the given position in this node. Either the left or the right
   * neighbor must be given for balancing. If this node is merged with one of its neighbors, the
   * middle element of the result array is {@code null}.
   * @param l left neighbor, possibly {@code null}
   * @param r right neighbor, possibly {@code null}
   * @param pos position of the element to delete
   * @return three-element array with the new left neighbor, node and right neighbor
   */
  protected abstract Node<N, E>[] remove(final Node<N, E> l, final Node<N, E> r, final long pos);

  /**
   * Extracts a sub-tree containing the elements at positions {@code off .. off + len - 1}
   * from the tree rooted at this node.
   * This method is only called if {@code len < this.size()} holds.
   * @param off offset of first element
   * @param len number of elements
   * @return the sub-tree, possibly under-full
   */
  protected abstract NodeLike<N, E> slice(final long off, final long len);

  /**
   * Returns a version of this node where the first sub-node is the given one.
   * @param newFirst new first sub-node
   * @return resulting node
   */
  protected abstract Node<N, E> replaceFirst(N newFirst);

  /**
   * Returns a version of this node where the last sub-node is the given one.
   * @param newLast new last sub-node
   * @return resulting node
   */
  protected abstract Node<N, E> replaceLast(N newLast);

  /**
   * Checks that this node does not violate any invariants.
   * @return this node's size
   * @throws AssertionError if an invariant was violated
   */
  protected abstract long checkInvariants();

  @Override
  public final NodeIterator<E> iterator() {
    return new NodeIterator<>(this, false);
  }

  /**
   * Creates a {@link ListIterator} over the elements in this node.
   * @param reverse flag for starting at the back of this node
   * @return the list iterator
   */
  public final NodeIterator<E> listIterator(final boolean reverse) {
    return new NodeIterator<>(this, reverse);
  }
}
