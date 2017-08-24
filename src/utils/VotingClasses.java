package utils;

class VotingClasses {
    private Object className;
    private int votes;

    public VotingClasses(Object className) {
        this.className = className;
    }


    public void vote() {
        this.votes++;
    }

    public int getVotes() {
        return this.votes;
    }

    public Object getClassName() {
        return className;
    }
}
