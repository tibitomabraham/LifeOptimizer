package tu_chemnitz.tibitometit.optimizer;

import java.util.Date;

/**
 * Created by TibiTom on 28-12-2016.
 */

public class Tasks {
    private int _id;
    private String _name;
    private String _description;
    private Date _deadline;
    private String _category;
    private boolean _status;
    private boolean _cancelled;

    public Tasks(String _name, String _description, Date _deadline, String _category, boolean _repeat) {
        this.set_name(_name);
        this.set_description(_description);
        this.set_deadline(_deadline);
        this.set_category(_category);
        this.set_status(false);
        this.set_cancelled(false);
    }
    public Tasks(){
    }

    public int get_id() {
        return _id;
    }

    public String get_name() {
        return _name;
    }

    public String get_description() {
        return _description;
    }

    public Date get_deadline() {
        return _deadline;
    }

    public String get_category() {
        return _category;
    }

    public boolean is_status() {
        return _status;
    }

    public boolean is_cancelled() {
        return _cancelled;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public void set_deadline(Date _deadline) {
        this._deadline = _deadline;
    }

    public void set_category(String _category) {
        this._category = _category;
    }

    public void set_status(boolean _status) {
        this._status = _status;
    }

    public void set_cancelled(boolean _cancelled) {
        this._cancelled = _cancelled;
    }
}
