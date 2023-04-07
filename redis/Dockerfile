FROM redis:6.2-alpine

# Copy the run.sh script into the container
COPY scripts/run.sh /

# Make the script executable
RUN chmod +x /run.sh

# Run the script when the container starts
CMD ["/run.sh"]