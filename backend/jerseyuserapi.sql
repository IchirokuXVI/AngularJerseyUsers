-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 05-01-2021 a las 01:23:58
-- Versión del servidor: 10.4.11-MariaDB
-- Versión de PHP: 7.4.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `jerseyuserapi`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `customdesignoptions`
--

CREATE TABLE `customdesignoptions` (
  `id` int(11) NOT NULL,
  `name` varchar(40) NOT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `customdesignoptions`
--

INSERT INTO `customdesignoptions` (`id`, `name`, `description`) VALUES
(3, 'Sticky navbar', 'Make navbar sticky so the main content will move when opening the navbar'),
(4, 'Dark mode', 'Activate dark mode (gray and black)'),
(8, 'John Doe', 'Test option to fill the table'),
(9, 'Lorem Ipsum', 'Test option to fill the table lorem ipsum dolor sit amet'),
(10, 'Another filler', 'As you may suppose those filler options don\'t do anything when you activate them');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `refresh_token`
--

CREATE TABLE `refresh_token` (
  `token_signature` char(64) NOT NULL,
  `user_id` int(11) NOT NULL,
  `revoked` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` char(128) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `user_customdesignoptions`
--

CREATE TABLE `user_customdesignoptions` (
  `user_id` int(11) NOT NULL,
  `option_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `customdesignoptions`
--
ALTER TABLE `customdesignoptions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indices de la tabla `refresh_token`
--
ALTER TABLE `refresh_token`
  ADD PRIMARY KEY (`token_signature`),
  ADD KEY `user_id` (`user_id`);

--
-- Indices de la tabla `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indices de la tabla `user_customdesignoptions`
--
ALTER TABLE `user_customdesignoptions`
  ADD PRIMARY KEY (`user_id`,`option_id`),
  ADD KEY `option_id` (`option_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `customdesignoptions`
--
ALTER TABLE `customdesignoptions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `refresh_token`
--
ALTER TABLE `refresh_token`
  ADD CONSTRAINT `refresh_token_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `user_customdesignoptions`
--
ALTER TABLE `user_customdesignoptions`
  ADD CONSTRAINT `user_customdesignoptions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `user_customdesignoptions_ibfk_2` FOREIGN KEY (`option_id`) REFERENCES `customdesignoptions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
