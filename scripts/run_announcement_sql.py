import os
import pathlib
import pymysql


def split_sql(sql_text: str):
    parts = []
    chunk = []
    for line in sql_text.splitlines():
        striped = line.strip()
        if striped.startswith("--") or not striped:
            continue
        chunk.append(line)
    merged = "\n".join(chunk)
    for part in merged.split(";"):
        stmt = part.strip()
        if stmt:
            parts.append(stmt)
    return parts


def main():
    sql_path = pathlib.Path("docs/sql/2026-04-16-blog-announcement-init.sql")
    sql_text = sql_path.read_text(encoding="utf-8")
    statements = split_sql(sql_text)

    conn = pymysql.connect(
        host=os.getenv("SOURCELIN_DB_HOST", "127.0.0.1"),
        port=int(os.getenv("SOURCELIN_DB_PORT", "3306")),
        user=os.getenv("SOURCELIN_DB_USER", "root"),
        password=os.getenv("SOURCELIN_DB_PASSWORD", ""),
        database=os.getenv("SOURCELIN_DB_NAME", "sourcelin-cloud"),
        charset="utf8mb4",
        autocommit=False,
    )
    try:
        with conn.cursor() as cur:
            for stmt in statements:
                cur.execute(stmt)
        conn.commit()
    finally:
        conn.close()

    conn = pymysql.connect(
        host=os.getenv("SOURCELIN_DB_HOST", "127.0.0.1"),
        port=int(os.getenv("SOURCELIN_DB_PORT", "3306")),
        user=os.getenv("SOURCELIN_DB_USER", "root"),
        password=os.getenv("SOURCELIN_DB_PASSWORD", ""),
        database=os.getenv("SOURCELIN_DB_NAME", "sourcelin-cloud"),
        charset="utf8mb4",
    )
    try:
        with conn.cursor() as cur:
            cur.execute(
                "select table_name from information_schema.tables "
                "where table_schema='sourcelin-cloud' and table_name in ('announcement','announcement_read') "
                "order by table_name"
            )
            tables = cur.fetchall()
            cur.execute(
                "select dict_type, count(1) from sys_dict_data "
                "where dict_type in ('blog_announcement_scope_type','blog_announcement_publish_status') "
                "group by dict_type order by dict_type"
            )
            dict_counts = cur.fetchall()
            cur.execute(
                "select dict_type, dict_name from sys_dict_type "
                "where dict_type in ('blog_announcement_scope_type','blog_announcement_publish_status') "
                "order by dict_type"
            )
            dict_types = cur.fetchall()
    finally:
        conn.close()

    print("executed_statements=", len(statements))
    print("tables=", tables)
    print("dict_types=", dict_types)
    print("dict_data_counts=", dict_counts)


if __name__ == "__main__":
    main()
