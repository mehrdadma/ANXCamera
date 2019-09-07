.class public final Landroid/support/v4/text/BidiFormatter$Builder;
.super Ljava/lang/Object;
.source "BidiFormatter.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/text/BidiFormatter;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "Builder"
.end annotation


# instance fields
.field private mFlags:I

.field private mIsRtlContext:Z

.field private mTextDirectionHeuristicCompat:Landroid/support/v4/text/TextDirectionHeuristicCompat;


# direct methods
.method public constructor <init>()V
    .locals 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    invoke-static {}, Ljava/util/Locale;->getDefault()Ljava/util/Locale;

    move-result-object v0

    invoke-static {v0}, Landroid/support/v4/text/BidiFormatter;->isRtlLocale(Ljava/util/Locale;)Z

    move-result v0

    invoke-direct {p0, v0}, Landroid/support/v4/text/BidiFormatter$Builder;->initialize(Z)V

    return-void
.end method

.method public constructor <init>(Ljava/util/Locale;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    invoke-static {p1}, Landroid/support/v4/text/BidiFormatter;->isRtlLocale(Ljava/util/Locale;)Z

    move-result p1

    invoke-direct {p0, p1}, Landroid/support/v4/text/BidiFormatter$Builder;->initialize(Z)V

    return-void
.end method

.method public constructor <init>(Z)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    invoke-direct {p0, p1}, Landroid/support/v4/text/BidiFormatter$Builder;->initialize(Z)V

    return-void
.end method

.method private static getDefaultInstanceFromContext(Z)Landroid/support/v4/text/BidiFormatter;
    .locals 0

    if-eqz p0, :cond_0

    sget-object p0, Landroid/support/v4/text/BidiFormatter;->DEFAULT_RTL_INSTANCE:Landroid/support/v4/text/BidiFormatter;

    goto :goto_0

    :cond_0
    sget-object p0, Landroid/support/v4/text/BidiFormatter;->DEFAULT_LTR_INSTANCE:Landroid/support/v4/text/BidiFormatter;

    :goto_0
    return-object p0
.end method

.method private initialize(Z)V
    .locals 0

    iput-boolean p1, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mIsRtlContext:Z

    sget-object p1, Landroid/support/v4/text/BidiFormatter;->DEFAULT_TEXT_DIRECTION_HEURISTIC:Landroid/support/v4/text/TextDirectionHeuristicCompat;

    iput-object p1, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mTextDirectionHeuristicCompat:Landroid/support/v4/text/TextDirectionHeuristicCompat;

    const/4 p1, 0x2

    iput p1, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mFlags:I

    return-void
.end method


# virtual methods
.method public build()Landroid/support/v4/text/BidiFormatter;
    .locals 3

    iget v0, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mFlags:I

    const/4 v1, 0x2

    if-ne v0, v1, :cond_0

    iget-object v0, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mTextDirectionHeuristicCompat:Landroid/support/v4/text/TextDirectionHeuristicCompat;

    sget-object v1, Landroid/support/v4/text/BidiFormatter;->DEFAULT_TEXT_DIRECTION_HEURISTIC:Landroid/support/v4/text/TextDirectionHeuristicCompat;

    if-ne v0, v1, :cond_0

    iget-boolean p0, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mIsRtlContext:Z

    invoke-static {p0}, Landroid/support/v4/text/BidiFormatter$Builder;->getDefaultInstanceFromContext(Z)Landroid/support/v4/text/BidiFormatter;

    move-result-object p0

    return-object p0

    :cond_0
    new-instance v0, Landroid/support/v4/text/BidiFormatter;

    iget-boolean v1, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mIsRtlContext:Z

    iget v2, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mFlags:I

    iget-object p0, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mTextDirectionHeuristicCompat:Landroid/support/v4/text/TextDirectionHeuristicCompat;

    invoke-direct {v0, v1, v2, p0}, Landroid/support/v4/text/BidiFormatter;-><init>(ZILandroid/support/v4/text/TextDirectionHeuristicCompat;)V

    return-object v0
.end method

.method public setTextDirectionHeuristic(Landroid/support/v4/text/TextDirectionHeuristicCompat;)Landroid/support/v4/text/BidiFormatter$Builder;
    .locals 0

    iput-object p1, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mTextDirectionHeuristicCompat:Landroid/support/v4/text/TextDirectionHeuristicCompat;

    return-object p0
.end method

.method public stereoReset(Z)Landroid/support/v4/text/BidiFormatter$Builder;
    .locals 0

    if-eqz p1, :cond_0

    iget p1, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mFlags:I

    or-int/lit8 p1, p1, 0x2

    iput p1, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mFlags:I

    goto :goto_0

    :cond_0
    iget p1, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mFlags:I

    and-int/lit8 p1, p1, -0x3

    iput p1, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mFlags:I

    :goto_0
    return-object p0
.end method