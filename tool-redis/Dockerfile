FROM docker.io/redis:6.2-alpine
# Create directory to use
WORKDIR /startup
# Copy the run.sh script into the container
COPY scripts/run.sh init.sh
# Make the script executable
RUN chmod +x /startup/init.sh
# Run the script when the container starts
ENTRYPOINT ["sh", "/startup/init.sh"]