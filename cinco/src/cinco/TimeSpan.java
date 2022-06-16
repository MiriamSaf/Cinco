package cinco;

import java.util.Date;

public class TimeSpan {
    public Date startDate;
    public Date endDate;

    @Override
    public String toString() {
        if (startDate == null || endDate == null) {
            return super.toString();
        }

        return String.format("%1$tF", this.startDate) + "–" + String.format("%1$tF", this.endDate);
    }
}
