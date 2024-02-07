INSERT INTO categories (id, name, description) VALUES (1, 'asd1', 'asdasd');
INSERT INTO categories (id, name, description) VALUES (2, 'asd2', 'asdasd');

INSERT INTO posts (id, title, description, content, category_id) VALUES (1, 'Mi mejor verano', 'El año pasado fui a Marruecos a hacer turismo', 'Mucho texto y muchas imagenes', 1);
INSERT INTO posts (id, title, description, content) VALUES (2, 'Receta Arroz', 'Tutorial avanzado para hacer arroz', 'Ingredientes y preparación');
INSERT INTO posts (id, title, description, content) VALUES (3, 'Ejercicio gimnasio', 'Rutina de musculatura para gimnasio', 'Tablas y tiempos para hacer en el gimnasio');

INSERT INTO comments (id, post_id, name, email, body) VALUES (1, 1, 'Sebastian', 'sebas@gmail.com', 'Me ha gustado la lectura de esta experiencia');
INSERT INTO comments (id, post_id, name, email, body) VALUES (2, 1, 'Christian', 'cristian@gmail.com', 'Intentaré hacerlo el año que viene');
INSERT INTO comments (id, post_id, name, email, body) VALUES (3, 1, 'Alejandro', 'alejandro@gmail.com', 'Me ha encantado');
INSERT INTO comments (id, post_id, name, email, body) VALUES (4, 2, 'Marco', 'marco@gmail.com', 'Una experiencia unica');

insert into users (id, name, username, email, password) values (1, 'Leticia', 'lcroxton0', 'loliva0@europa.eu', '$2a$04$/Qpy.M7Xg3ksrC6MvKYHeOMbTkBEBmXYdOaERRVGLgm/0mIL1CP1.');
insert into users (id, name, username, email, password) values (2, 'Faunie', 'fspincke1', 'fyuryev1@senate.gov', '$2a$04$Q7fd8kNaFpGUEuHd1/ukZu6Ho7FvDby/u4G7cBgC8xr47bdFSp6r.');
insert into users (id, name, username, email, password) values (3, 'Tades', 'tchallicum2', 'tkiddy2@1und1.de', '$2a$04$CNKxyEeHC0VGm4hpDgx1cO2sx2dSf.qRqQd9pZZe/0WEme/C/KG5i');
insert into users (id, name, username, email, password) values (4, 'Leonore', 'lquick3', 'lklamman3@ezinearticles.com', '$2a$04$9kQULjLmwXjua92iDZclF.UMQVfYd25lTB7v5f8CvHOy2wwiZt1h6');
insert into users (id, name, username, email, password) values (5, 'Brigit', 'bsanti4', 'bmatejic4@youku.com', '$2a$04$kZVI1yFE92tr3dQSJd6zze.tpvqPZv51HtV8BXcL3xHIOOqNpIVPK');
insert into users (id, name, username, email, password) values (6, 'Shayne', 'slawden5', 'soaker5@addthis.com', '$2a$04$Qbni2vSWt9fHlT1YaXDnX.Gzpv9Wf2rdTDEvfR22rn4CeeWAHHC/i');

INSERT INTO roles (id, name) VALUES(1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES(2, 'ROLE_USER');

INSERT INTO users_roles (role_id, user_id) VALUES (1, 1);
INSERT INTO users_roles (role_id, user_id) VALUES (1, 2);
INSERT INTO users_roles (role_id, user_id) VALUES (1, 3);
INSERT INTO users_roles (role_id, user_id) VALUES (2, 4);
INSERT INTO users_roles (role_id, user_id) VALUES (2, 5);
INSERT INTO users_roles (role_id, user_id) VALUES (2, 6);
