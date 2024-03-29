# Dockerfile for the use in the Jenkinsfile for the DBC-assertions project,
# to set up the build environment for Jenkins to use.

# © Copyright Benedict Adamson 2018-22.
# 
# This file is part of DBC-assertions.
#
# DBC-assertions is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# DBC-assertions is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with DBC-assertions.  If not, see <https://www.gnu.org/licenses/>.
#

FROM debian:11
RUN apt-get -y update && apt-get -y install \
   maven \
   openjdk-11-jdk-headless