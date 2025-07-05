package me.tasy5kg.cutegif.components.preview

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class ItemTouchHelperCallback(private val adapter: MediaGridAdapter): ItemTouchHelper.Callback() {
  private var dragView: View? = null
  private var dragFrom = -1
  private var dragTo = -1

  override fun getMovementFlags(recyclerView:RecyclerView, viewHolder : RecyclerView.ViewHolder):Int {
      val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                      ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
      return makeMovementFlags(dragFlags, 0)
  }

  override fun onMove(recyclerView: RecyclerView,
                      source:RecyclerView.ViewHolder,
                      target:RecyclerView.ViewHolder):Boolean {
    if (dragFrom == -1) {
      dragFrom = source.absoluteAdapterPosition;
      dragView = source.itemView
      dragView?.elevation = 16f; // 提升被拖拽项的层级
    }
    dragTo = target.absoluteAdapterPosition

    // 根据模式执行移动操作
    if (adapter.getSwapMode()) {
      adapter.swapItems(source.absoluteAdapterPosition, target.absoluteAdapterPosition);
    } else {
      adapter.insertItem(source.absoluteAdapterPosition, target.absoluteAdapterPosition);
    }

    return true;
  }

  override fun onSwiped(viewHolder:RecyclerView.ViewHolder, direction:Int) {
      // 不需要实现
  }

  override fun isLongPressDragEnabled(): Boolean {
      return true;
  }

  override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
    super.onSelectedChanged(viewHolder, actionState)
    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
      viewHolder?.itemView?.alpha = 0.6f
      viewHolder?.itemView?.scaleX = 0.95f;
      viewHolder?.itemView?.scaleY = 0.95f;
    }
  }

  override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
    super.clearView(recyclerView, viewHolder)
    viewHolder.itemView.alpha = 1.0f
    viewHolder.itemView.scaleX = 1f;
    viewHolder.itemView.scaleY = 1f;

    // 拖拽结束
    if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
      adapter.getMoveListener()?.onItemDropped();
    }

    dragView?.elevation = 4f; // 恢复原始层级

    dragFrom = -1;
    dragTo = -1;
    dragView = null;
  }

}