/*
 *
 *  o                        o     o   o         o
 *  |             o          |     |\ /|         | /
 *  |    o-o o--o    o-o  oo |     | O |  oo o-o OO   o-o o   o
 *  |    | | |  | | |    | | |     |   | | | |   | \  | |  \ /
 *  O---oo-o o--O |  o-o o-o-o     o   o o-o-o   o  o o-o   o
 *              |
 *           o--o
 *  o--o              o               o--o       o    o
 *  |   |             |               |    o     |    |
 *  O-Oo   oo o-o   o-O o-o o-O-o     O-o    o-o |  o-O o-o
 *  |  \  | | |  | |  | | | | | |     |    | |-' | |  |  \
 *  o   o o-o-o  o  o-o o-o o o o     o    | o-o o  o-o o-o
 *
 *  Logical Markov Random Fields (LoMRF).
 *
 *
 */

package lomrf.util.collection

import scala.{ specialized => sp }

trait KeyPartitioned[C, @sp(Byte, Short, Int, Long) K, @sp(Byte, Short, Int, Long) V] extends (K => V) {

  def apply(key: K): V

  def contains(key: K): Boolean

  def size: Int

  def partitions: Iterable[C]

  def partition(partitionIndex: Int): C

}

object KeyPartitioned {

  def apply[C, @sp(Byte, Short, Int, Long) K, @sp(Byte, Short, Int, Long) V](data: Array[C], partitioner: Partitioner[K], partitionFetcher: PartitionFetcher[K, C, V]): KeyPartitioned[C, K, V] = {
    new KeyPartitionedImpl[C, K, V](data, partitioner, partitionFetcher)
  }

  private class KeyPartitionedImpl[C, @sp(Byte, Short, Int, Long) K, @sp(Byte, Short, Int, Long) V](data: Array[C], partitioner: Partitioner[K], partitionFetcher: PartitionFetcher[K, C, V]) extends KeyPartitioned[C, K, V] {

    override def apply(key: K): V = partitionFetcher(key, data(partitioner(key)))

    override def contains(key: K): Boolean = partitionFetcher.contains(key, data(partitioner(key)))

    override def partitions: Iterable[C] = data.toIterable

    override def partition(partitionIndex: Int): C = data(partitionIndex)

    override def size: Int = data.length
  }

}
