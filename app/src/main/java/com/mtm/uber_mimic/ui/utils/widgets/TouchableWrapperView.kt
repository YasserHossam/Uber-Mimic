package com.mtm.uber_mimic.ui.utils.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.view.MotionEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TouchableWrapperView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private val _isViewTouchedLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val isViewTouchedLiveData: LiveData<Boolean> = _isViewTouchedLiveData

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> _isViewTouchedLiveData.postValue(true)
        }
        return super.dispatchTouchEvent(event)
    }
}