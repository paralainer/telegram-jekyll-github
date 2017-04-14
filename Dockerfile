FROM maven:3.5-jdk-8-onbuild


COPY docker_ssh /root/.ssh
RUN git config --global push.default "simple"

ENTRYPOINT git config --global user.email $GIT_EMAIL && \
git config --global user.name $GIT_USER && \
eval "$(ssh-agent -s)" && \
ssh-add /root/.ssh/id_rsa && \
ssh-keyscan -t rsa github.com >> /root/.ssh/known_hosts && \
rm -rf /var/blog && \
git clone $BLOG_REPO /var/blog && \
java -jar /usr/src/app/target/telegram-jekyll-github-1.0-SNAPSHOT-jar-with-dependencies.jar