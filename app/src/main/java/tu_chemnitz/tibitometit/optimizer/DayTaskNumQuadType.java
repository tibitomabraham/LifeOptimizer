package tu_chemnitz.tibitometit.optimizer;

/**
 * Created by TibiTom on 14-01-2017.
 */

public class DayTaskNumQuadType {
    int iuTasks;
    int iTasks;
    int uTasks;
    int nTasks;
    int totalTasks;
    int compIUTasks;
    int compITasks;
    int compUTasks;
    int compNTasks;
    int compTotalTasks;
    DayTaskNumQuadType(){
        this.iuTasks=0;
        this.iTasks=0;
        this.uTasks=0;
        this.nTasks=0;
        this.totalTasks=0;
        this.compIUTasks=0;
        this.compITasks=0;
        this.compUTasks=0;
        this.compNTasks=0;
        this.compTotalTasks=0;
    }

    public int getCompIUTasks() {
        return compIUTasks;
    }

    public int getCompNTasks() {
        return compNTasks;
    }

    public int getCompTotalTasks() {
        return compTotalTasks;
    }

    public int getCompUTasks() {
        return compUTasks;
    }

    public int getiTasks() {
        return iTasks;
    }

    public int getIuTasks() {
        return iuTasks;
    }

    public int getnTasks() {
        return nTasks;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getuTasks() {
        return uTasks;
    }

    public int getCompITasks() {

        return compITasks;
    }

    public void setCompITasks(int compITasks) {
        this.compITasks = compITasks;
    }

    public void setCompIUTasks(int compIUTasks) {
        this.compIUTasks = compIUTasks;
    }

    public void setCompNTasks(int compNTasks) {
        this.compNTasks = compNTasks;
    }

    public void setCompTotalTasks(int compTotalTasks) {
        this.compTotalTasks = compTotalTasks;
    }

    public void setCompUTasks(int compUTasks) {
        this.compUTasks = compUTasks;
    }

    public void setiTasks(int iTasks) {
        this.iTasks = iTasks;
    }

    public void setIuTasks(int iuTasks) {
        this.iuTasks = iuTasks;
    }

    public void setnTasks(int nTasks) {
        this.nTasks = nTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public void setuTasks(int uTasks) {
        this.uTasks = uTasks;
    }
}
