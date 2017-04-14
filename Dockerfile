FROM maven:3.5-jdk-8-onbuild


COPY docker_ssh /root/.ssh
RUN git config --global user.email "serg.talov@gmail.com"
RUN git config --global user.name "paralainer"
RUN git config --global push.default "simple"

ENTRYPOINT eval "$(ssh-agent -s)" && \
echo $SSH_AGENT_PID && ssh-add /root/.ssh/id_rsa && \
ssh-keyscan -t rsa github.com >> /root/.ssh/known_hosts && \
git clone git@github.com:paralainer/paralainer.github.io.git /var/blog && \
java -jar /usr/src/app/target/telegram-jekyll-github-1.0-SNAPSHOT-jar-with-dependencies.jar