
CREATE OR REPLACE FUNCTION trunc_year(timestamp with time zone) RETURNS timestamp with time zone
AS 'select date_trunc(''year'',$1)'
    LANGUAGE SQL
    IMMUTABLE
    RETURNS NULL ON NULL INPUT;;

CREATE OR REPLACE FUNCTION trunc_month(timestamp with time zone) RETURNS timestamp with time zone
AS 'select date_trunc(''month'',$1)'
    LANGUAGE SQL
    IMMUTABLE
    RETURNS NULL ON NULL INPUT;;

CREATE OR REPLACE FUNCTION trunc_day(timestamp with time zone) RETURNS timestamp with time zone
AS 'select date_trunc(''day'',$1)'
    LANGUAGE SQL
    IMMUTABLE
    RETURNS NULL ON NULL INPUT;;

CREATE OR REPLACE FUNCTION trunc_week(timestamp with time zone) RETURNS timestamp with time zone
AS 'select date_trunc(''week'',$1)'
    LANGUAGE SQL
    IMMUTABLE
    RETURNS NULL ON NULL INPUT;;

CREATE OR REPLACE FUNCTION trunc_hour(timestamp with time zone) RETURNS timestamp with time zone
AS 'select date_trunc(''hour'',$1)'
    LANGUAGE SQL
    IMMUTABLE
    RETURNS NULL ON NULL INPUT;;

CREATE OR REPLACE FUNCTION trunc_minute(timestamp with time zone) RETURNS timestamp with time zone
AS 'select date_trunc(''minute'',$1)'
    LANGUAGE SQL
    IMMUTABLE
    RETURNS NULL ON NULL INPUT;;
CREATE OR REPLACE FUNCTION jsonb_extract_path_as_numeric(jsonb_data jsonb, VARIADIC path_elements text[])
    RETURNS NUMERIC AS $$
BEGIN
    RETURN CAST(jsonb_extract_path(jsonb_data, VARIADIC path_elements) AS NUMERIC);
END;
$$ LANGUAGE plpgsql;
