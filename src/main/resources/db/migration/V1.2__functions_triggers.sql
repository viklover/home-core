
CREATE OR REPLACE FUNCTION object_update_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION object_update_timestamp_from_another_layer()
    RETURNS TRIGGER AS $$
BEGIN
    UPDATE object SET updated_at = NOW() WHERE id = NEW.object_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_timestamp BEFORE UPDATE ON object
    FOR EACH ROW EXECUTE PROCEDURE object_update_timestamp();

CREATE TRIGGER set_timestamp BEFORE UPDATE ON t_object
    FOR EACH ROW EXECUTE PROCEDURE object_update_timestamp_from_another_layer();
CREATE TRIGGER set_timestamp BEFORE UPDATE ON input
    FOR EACH ROW EXECUTE PROCEDURE object_update_timestamp_from_another_layer();
CREATE TRIGGER set_timestamp BEFORE UPDATE ON output
    FOR EACH ROW EXECUTE PROCEDURE object_update_timestamp_from_another_layer();
CREATE TRIGGER set_timestamp BEFORE UPDATE ON trigger
    FOR EACH ROW EXECUTE PROCEDURE object_update_timestamp_from_another_layer();

CREATE TRIGGER set_timestamp BEFORE UPDATE ON v_object
    FOR EACH ROW EXECUTE PROCEDURE object_update_timestamp_from_another_layer();
CREATE TRIGGER set_timestamp BEFORE UPDATE ON onewire
    FOR EACH ROW EXECUTE PROCEDURE object_update_timestamp_from_another_layer();
CREATE TRIGGER set_timestamp BEFORE UPDATE ON onewire__serial
    FOR EACH ROW EXECUTE PROCEDURE object_update_timestamp_from_another_layer();
