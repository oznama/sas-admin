CREATE OR REPLACE FUNCTION f_unaccent(
    accent_word character varying)
    RETURNS character varying
    LANGUAGE 'sql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
SELECT REPLACE(
    REPLACE(
        REPLACE(
            REPLACE(
                REPLACE(
                    REPLACE(
                        REPLACE(
                            REPLACE(
                                REPLACE(
                                    REPLACE(accent_word,
                                        'á','a'),
                                    'é','e' ),
                                'í','i'),
                            'ó','o'),
                        'ú', 'u'),
                    'Á', 'A'),
                'É', 'E'),
            'Í', 'I'),
        'Ó', 'O'),
    'Ú', 'U');
$BODY$;