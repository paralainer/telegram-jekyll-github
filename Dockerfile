FROM maven:3.5-jdk-8-onbuild


COPY docker_ssh /root/.ssh
RUN eval "$(ssh-agent -s)"
RUN ssh-add /root/.ssh/id_rsa
RUN git config --global user.email "serg.talov@gmail.com"
RUN git config --global user.name "paralainer"
RUN git config --global push.default "simple"
RUN git clone https://github.com/paralainer/paralainer.github.io.git /var/blog

ENTRYPOINT java -jar /usr/src/app/target/telegram-jekyll-github-1.0-SNAPSHOT-jar-with-dependencies.jar