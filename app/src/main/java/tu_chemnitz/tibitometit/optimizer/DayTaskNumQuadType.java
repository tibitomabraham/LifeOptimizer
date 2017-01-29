package tu_chemnitz.tibitometit.optimizer;

/**
 * Created by TibiTom on 14-01-2017.
 */

class DayTaskNumQuadType {
    int totalTasks;
    int compTotalTasks;
    DayTaskNumQuadType(){
        this.totalTasks=0;
        this.compTotalTasks=0;
    }

    public int getCompTotalTasks() {
        return compTotalTasks;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setCompTotalTasks(int compTotalTasks) {
        this.compTotalTasks = compTotalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

}
