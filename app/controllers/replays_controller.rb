class ReplaysController < ApplicationController
  # GET /replays
  # GET /replays.xml
  def index
    @replays = Replay.all.reverse

    respond_to do |format|
      format.html # index.html.erb
      format.xml
    end
  end

  # GET /replays/1
  # GET /replays/1.xml
  def show
    @replay = Replay.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :text => @replay.xml }
    end
  end

  # GET /replays/new
  # GET /replays/new.xml
  def new
    @replay = Replay.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @replay }
    end
  end

  # GET /replays/1/edit
  def edit
    @replay = Replay.find(params[:id])
  end

  # POST /replays
  # POST /replays.xml
  def create
    @replay = Replay.new(params[:replay])

    respond_to do |format|
      if @replay.save
        format.html { redirect_to(@replay, :notice => 'Replay was successfully created.') }
        format.xml  { render :xml => @replay, :status => :created, :location => @replay }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @replay.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /replays/1
  # PUT /replays/1.xml
  def update
    @replay = Replay.find(params[:id])

    respond_to do |format|
      if @replay.update_attributes(params[:replay])
        format.html { redirect_to(@replay, :notice => 'Replay was successfully updated.') }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @replay.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /replays/1
  # DELETE /replays/1.xml
  def destroy
    @replay = Replay.find(params[:id])
    @replay.destroy

    respond_to do |format|
      format.html { redirect_to(replays_url) }
      format.xml  { head :ok }
    end
  end
end
