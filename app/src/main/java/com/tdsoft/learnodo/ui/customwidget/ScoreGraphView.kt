package com.tdsoft.learnodo.ui.customwidget

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View

const val MARGIN = 100f

class ScoreGraphView : View {

    constructor(context: Context)
            : super(context, null, android.R.attr.radioButtonStyle) {
        initScoreGraphView()
    }


    constructor(context: Context, attrs: AttributeSet)
            : super(context, attrs, android.R.attr.radioButtonStyle) {
        initScoreGraphView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initScoreGraphView()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        initScoreGraphView()
    }

    private var totalStudents: Float = 0.0f
    private var lawMarksStudents: Float = 0.0f
    private var averageMarksStudents: Float = 0.0f
    private var highMarksStudents: Float = 0.0f

    private var marks = arrayOf(2, 4, 4)
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private val paintLines = Paint()
    private val paintText = Paint()
    private val paintFilled = Paint()

    private fun initScoreGraphView() {
        paintText.apply {
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.FILL
            textSize = 40f
        }
        paintLines.apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        paintFilled.apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.FILL
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawLine(MARGIN, 0f, MARGIN, viewHeight.toFloat() - MARGIN, paintLines)
        canvas?.drawLine(
            MARGIN,
            viewHeight.toFloat() - MARGIN,
            viewWidth.toFloat(),
            viewHeight.toFloat() - MARGIN,
            paintLines
        )
        canvas?.drawText(
            "Marks",
            (viewWidth / 2).toFloat() - 20,
            (viewHeight.toFloat() - 20),
            paintText
        )
        canvas?.save()

        canvas?.rotate(90F)
        val size = (paintText.measureText("Students") / 2) + 50
        canvas?.drawText(
            "Students",
            (viewHeight / 2F - size),
            -20f,
            paintText
        )
        // save canvas before restore
        canvas?.restore() // restore canvas
        canvas?.save()

        if(totalStudents > 0) {
            val lawMarksGraphVal = (lawMarksStudents / totalStudents * viewHeight)
            val averageMarksGraphVal = (averageMarksStudents / totalStudents * viewHeight)
            val highMarksGraphVal = (highMarksStudents / totalStudents * viewHeight)

            canvas?.drawRect(
                MARGIN,
                (viewHeight - MARGIN) - lawMarksGraphVal,
                MARGIN + 100f,
                viewHeight - MARGIN,
                paintFilled
            )
            canvas?.rotate(90F)
            canvas?.drawText(
                "Law",
                (viewHeight.toFloat()) - 90f,
                -(MARGIN + 30f),
                paintText
            )
            canvas?.restore() // restore canvas
            canvas?.save()


            canvas?.drawRect(
                MARGIN + 110f,
                (viewHeight - MARGIN) - averageMarksGraphVal,
                MARGIN + 110f + 100f,
                viewHeight.toFloat() - MARGIN,
                paintFilled
            )

            canvas?.rotate(90F)
            canvas?.drawText(
                "Ave",
                (viewHeight.toFloat()) - 90f,
                -(MARGIN + 110f + 30f),
                paintText
            )
            canvas?.restore() // restore canvas
            canvas?.save()


            canvas?.drawRect(
                MARGIN + 220f,
                (viewHeight - MARGIN) - highMarksGraphVal,
                MARGIN + 220f + 100f,
                viewHeight.toFloat() - MARGIN,
                paintFilled
            )
            canvas?.rotate(90F)
            canvas?.drawText(
                "High",
                (viewHeight.toFloat()) - 90f,
                -(MARGIN + 220f + 30f),
                paintText
            )
            // save canvas before restore
            canvas?.restore() // restore canvas
            canvas?.save()
        }
    }

    fun setMarks(lawMarksCount:Int, avMarksCount:Int, highMarksCount:Int){
        this.lawMarksStudents = lawMarksCount.toFloat()
        this.averageMarksStudents = avMarksCount.toFloat()
        this.highMarksStudents = highMarksCount.toFloat()
        this.totalStudents = lawMarksStudents + averageMarksStudents + highMarksStudents
        invalidate()
    }

    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        super.onSizeChanged(xNew, yNew, xOld, yOld)
        viewWidth = xNew
        viewHeight = yNew
    }
}

//students = 10
//low = 2
//average - 4
//High - 4
