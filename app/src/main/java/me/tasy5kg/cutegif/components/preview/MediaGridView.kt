package me.tasy5kg.cutegif.components.preview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.withStyledAttributes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.tasy5kg.cutegif.R


class MediaGridView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
  RecyclerView(context, attrs, defStyleAttr) {
  private var maxRows = 0
  private var maxColumns = 0
  private var itemSize = 0
  private var itemSpacing = 0
  private var labelHeight = 0
  private var labelTextSize = 0
  private var playButtonSize = 0

  private lateinit var adapter: MediaGridAdapter
  private val mediaItems: MutableList<MediaItem> = ArrayList<MediaItem>()

  init {
    init(context, attrs)
  }

  private fun init(context: Context, attrs: AttributeSet?) {
    // 获取自定义属性
    context.withStyledAttributes(attrs, R.styleable.MediaGridView) {

      maxRows = getInt(R.styleable.MediaGridView_maxRows, DEFAULT_MAX_ROWS)
      maxColumns = getInt(R.styleable.MediaGridView_maxColumns, DEFAULT_MAX_COLUMNS)

      itemSize = getDimensionPixelSize(
        R.styleable.MediaGridView_itemSize,
        dpToPx(context, DEFAULT_ITEM_SIZE_DP)
      )

      itemSpacing = getDimensionPixelSize(
        R.styleable.MediaGridView_itemSpacing,
        dpToPx(context, DEFAULT_ITEM_SPACING_DP)
      )

      labelHeight = getDimensionPixelSize(
        R.styleable.MediaGridView_labelHeight,
        dpToPx(context, DEFAULT_LABEL_HEIGHT_DP)
      )

      labelTextSize = getDimensionPixelSize(
        R.styleable.MediaGridView_labelTextSize,
        spToPx(context, DEFAULT_LABEL_TEXT_SIZE_SP)
      )

      playButtonSize = getDimensionPixelSize(
        R.styleable.MediaGridView_playButtonSize,
        dpToPx(context, DEFAULT_PLAY_BUTTON_SIZE_DP)
      )

    }

    // 设置布局管理器
    val layoutManager = GridLayoutManager(context, maxColumns)
    layoutManager.setOrientation(HORIZONTAL)
    setLayoutManager(layoutManager)

    // 设置适配器
    adapter = MediaGridAdapter(context, mediaItems, itemSize, labelHeight, labelTextSize, playButtonSize)
    setAdapter(adapter)

    // 添加间距装饰
    addItemDecoration(GridSpacingItemDecoration(maxColumns, itemSpacing, true))

    // 设置固定高度
    setHasFixedSize(true)
  }

  @SuppressLint("NotifyDataSetChanged")
  fun setMediaItems(items: List<MediaItem>) {
    this.mediaItems.clear()
    this.mediaItems.addAll(items)

    adapter.notifyDataSetChanged()
  }

  fun addMediaItem(item: MediaItem) {
    this.mediaItems.add(item)
    adapter.notifyItemInserted(mediaItems.size - 1)
  }

  @SuppressLint("NotifyDataSetChanged")
  fun clearMediaItems() {
    this.mediaItems.clear()
    adapter.notifyDataSetChanged()
  }

  private fun dpToPx(context: Context, dp: Int): Int {
    return TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      dp.toFloat(),
      context.resources.displayMetrics
    ).toInt()
  }

  private fun spToPx(context: Context, sp: Int): Int {
    return TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_SP,
      sp.toFloat(),
      context.resources.displayMetrics
    ).toInt()
  }

  // 网格间距装饰类
  private class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) : ItemDecoration() {
    override fun getItemOffsets(
      outRect: Rect, view: View,
      parent: RecyclerView,state: State
    ) {
      val position = parent.getChildAdapterPosition(view) // item position
      val column = position % spanCount // item column

      outRect.top = spacing
      outRect.bottom = spacing
      if (includeEdge) {
        outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
        outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)
      } else {
        outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
        outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
      }
    }
  }

  companion object {
    // 默认值
    private const val DEFAULT_MAX_ROWS = 3
    private const val DEFAULT_MAX_COLUMNS = 3
    private const val DEFAULT_ITEM_SIZE_DP = 120
    private const val DEFAULT_ITEM_SPACING_DP = 8
    private const val DEFAULT_LABEL_HEIGHT_DP = 28
    private const val DEFAULT_LABEL_TEXT_SIZE_SP = 12
    private const val DEFAULT_PLAY_BUTTON_SIZE_DP = 40
  }
}