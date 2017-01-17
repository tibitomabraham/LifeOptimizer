package tu_chemnitz.tibitometit.optimizer;

import java.util.Date;

/**
 * Created by TibiTom on 28-12-2016.
 */

class Tasks {
    private int _id;
    private String _name;
    private String _description;
    private Date _deadline;
    private String _category;
    private boolean _status;
    private boolean _cancelled;

    Tasks(String _name, String _description, Date _deadline, String _category, boolean _repeat) {
        this.set_name(_name);
        this.set_description(_description);
        this.set_deadline(_deadline);
        this.set_category(_category);
        this.set_status(false);
        this.set_cancelled(false);
    }
    Tasks(){
    }

    int get_id() {
        return _id;
    }

    String get_name() {
        return _name;
    }

    String get_description() {
        return _description;
    }

    Date get_deadline() {
        return _deadline;
    }

    String get_category() {
        return _category;
    }

    boolean is_status() {
        return _status;
    }

    boolean is_cancelled() {
        return _cancelled;
    }

    void set_id(int _id) {
        this._id = _id;
    }

    void set_name(String _name) {
        this._name = _name;
    }

    void set_description(String _description) {
        this._description = _description;
    }

    void set_deadline(Date _deadline) {
        this._deadline = _deadline;
    }

    void set_category(String _category) {
        this._category = _category;
    }

    void set_status(boolean _status) {
        this._status = _status;
    }

    void set_cancelled(boolean _cancelled) {
        this._cancelled = _cancelled;
    }
}

