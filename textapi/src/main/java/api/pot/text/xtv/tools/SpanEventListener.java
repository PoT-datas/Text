package api.pot.text.xtv.tools;

import android.text.style.ClickableSpan;
import android.view.View;

public abstract class SpanEventListener extends ClickableSpan {

    abstract public void onLongClick(View view);
}
